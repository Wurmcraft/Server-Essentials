package main

import (
	"fmt"
	"github.com/bwmarrin/discordgo"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
	"strings"
)

const permChat = "chat"

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
			broadcast <- BridgeMessage{
				Message:          m.Content,
				ID:               "discord",
				UserID:           m.Author.ID,
				DisplayName:      m.Author.Username,
				Channel:          channel,
				DiscordChannelID: m.ChannelID,
				FormattingStyle:  0,
			}
		}
	}
}

var clients = make(map[*websocket.Conn]string)
var broadcast = make(chan BridgeMessage)
var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
}

func MessageSocket(w http.ResponseWriter, r *http.Request) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permChat) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	ws, err := upgrader.Upgrade(w, r, nil)
	clients[ws] = w.Header().Get("serverID")
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
			if clients[client] != msg.ID {
				err := client.WriteJSON(msg)
				if err != nil {
					log.Printf("error: %v", err)
					client.Close()
					delete(clients, client)
				}
			}
		}
		if msg.ID != "discord" {
			handleDiscord(msg)
		}
	}
}
