package main

import (
	"encoding/json"
	"fmt"
	"github.com/bwmarrin/discordgo"
	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	"io/ioutil"
	"log"
	"net/http"
	"strings"
)

var storedMessages = make(map[string][]BridgeMessage)

const permChat = "chat"

func AddMessage(w http.ResponseWriter, r *http.Request) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permRank) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var message BridgeMessage
	err = json.Unmarshal(b, &message)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	AppendMessage(message)
	w.WriteHeader(http.StatusOK)
}

func GetMessages(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	serverID := vars["serverID"]
	if storedMessages[serverID] != nil {
		output, err := json.MarshalIndent(storedMessages[serverID], " ", " ")
		if err != nil {
			fmt.Fprintln(w, "{}")
			return
		}
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write(output)
		storedMessages[serverID] = []BridgeMessage{}
	} else {
		w.Write([]byte("[]"))
	}
}

func AppendMessage(message BridgeMessage) {
	for entry := range redisDBStatus.Keys(ctx, "*").Val() {
		var serverStatus ServerStatus
		json.Unmarshal([]byte(redisDBStatus.Get(ctx, redisDBStatus.Keys(ctx, "*").Val()[entry]).Val()), &serverStatus)
		if strings.EqualFold(serverStatus.ServerID, message.ID) {
			continue
		}
		if storedMessages[serverStatus.ServerID] != nil {
			storedMessages[serverStatus.ServerID] = append(storedMessages[serverStatus.ServerID], message)
		} else {
			storedMessages[serverStatus.ServerID] = []BridgeMessage{message}
		}
	}
	handleDiscord(message)
}

func handleDiscord(message BridgeMessage) {
	if !strings.EqualFold("discord", message.ID) {
		if discord == nil {
			dis, err := discordgo.New("Bot " + discordToken)
			if err != nil {
				fmt.Println(err.Error())
			}
			discord = dis
		}
		msg := ""
		if message.FormattingStyle == 0 {
			msg = "**[" + message.ID + "] " + removeFormatting(message.DisplayName) + "** " + message.Message
		} else if message.FormattingStyle == 1 {
			msg = "**" + removeFormatting(message.DisplayName) + "** " + message.Message
		} else if message.FormattingStyle == 2 {
			msg = "**" + removeFormatting(message.Message) + "**"
		} else if message.FormattingStyle == 3 {
			msg = removeFormatting(message.Message)
		}
		_, err := discord.ChannelMessageSend(message.DiscordChannelID, msg)
		if err != nil {
			fmt.Println(err.Error())
		}
	}
}

var replacer = strings.NewReplacer(
	"&0", "",
	"&1", "",
	"&2", "",
	"&3", "",
	"&4", "",
	"&5", "",
	"&6", "",
	"&7", "",
	"&8", "",
	"&9", "",
	"&a", "",
	"&b", "",
	"&c", "",
	"&d", "",
	"&e", "",
	"&f", "")

func removeFormatting(format string) string {
	if strings.Contains(format, "&") {
		return replacer.Replace(format)
	}
	return format
}

func handleChannelMessages(s *discordgo.Session, m *discordgo.MessageCreate) {
	if m.Author.ID == s.State.User.ID {
		return
	}
	for channelID, channel := range discordChannelMap {
		if strings.EqualFold(channelID, m.ChannelID) {
			AppendMessage(BridgeMessage{
				Message:          m.Content,
				ID:               "discord",
				UserID:           m.Author.ID,
				DisplayName:      m.Author.Username,
				Channel:          channel,
				DiscordChannelID: m.ChannelID,
				FormattingStyle:  0,
			})
		}
	}
}

var clients = make(map[*websocket.Conn]bool)
var broadcast = make(chan BridgeMessage)
var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
}

func MessageSocket(w http.ResponseWriter, r *http.Request) {
	ws, err := upgrader.Upgrade(w, r, nil)
	clients[ws] = true
	if err != nil {
		fmt.Println("Failed to update GET request to -> WebSocket")
	}
	for {
		var msg BridgeMessage
		err := ws.ReadJSON(&msg)
		if err != nil {
			log.Printf("error: %v", err)
			delete(clients, ws)
			break
		}
		broadcast <- msg
	}
	defer ws.Close()
}

func handleMessages() {
	for {
		msg := <-broadcast
		for client := range clients {
			err := client.WriteJSON(msg)
			if err != nil {
				log.Printf("error: %v", err)
				client.Close()
				delete(clients, client)
			}
		}
		handleDiscord(msg)
	}
}
