package main

import (
	"encoding/json"
	"fmt"
	"github.com/bwmarrin/discordgo"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
	"strings"
)

var storedMessages = make(map[string][]BridgeMessage)

const permChat = "chat"

func AddMessage(w http.ResponseWriter, r *http.Request, _ mux.Params) {
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

func GetMessages(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	serverID := string(p[0].Value)
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
			msg = "**[" + message.ID + "] " + message.DisplayName + "** " + message.Message
		} else if message.FormattingStyle == 1 {
			msg = "**" + message.DisplayName + "** " + message.Message
		}
		_, err := discord.ChannelMessageSend(message.DiscordChannelID, msg)
		if err != nil {
			fmt.Println(err.Error())
		}
	}
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
