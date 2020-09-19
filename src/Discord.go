package main

import (
	"encoding/json"
	"fmt"
	"github.com/bwmarrin/discordgo"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"
)

var verifiedUsers map[string]string

func startupBot() {
	discord, err := discordgo.New("Bot " + discordToken)
	if err != nil {
		fmt.Println("Unable to startup Discord bot! (" + err.Error() + ")")
	}
	err = discord.Open()
	if err != nil {
		fmt.Println("error opening connection,", err)
		return
	}

	discord.AddHandler(handleVerifyDMs)
	discord.AddHandler(handleCommands)
	discord.AddHandler(handleServerInputCommands)

	fmt.Println("Discord Bot is now running.")

	verifyUsers(discord)
	fmt.Println("Checking for user's to verify!")
	sc := make(chan os.Signal, 1)
	signal.Notify(sc, syscall.SIGINT, syscall.SIGTERM, os.Interrupt, os.Kill)
	<-sc
	discord.Close()
}

func verifyUsers(s *discordgo.Session) {
	verifiedUsers = map[string]string{}
	for range time.Tick(time.Minute * 5) {
		populateVerifiedList()
		for a := range verifiedUsers {
			if len(a) > 0 {
				member, err := s.State.Member(discordServerID, a)
				role, err2 := s.State.Role(discordServerID, discordRoleID)
				if err != nil {
					fmt.Println(err.Error())
					continue
				}
				if err2 != nil {
					fmt.Println(err2.Error())
					continue
				}
				if !contains(member.Roles, role.Name) {
					s.GuildMemberRoleAdd(discordServerID, a, discordRoleID)
					dmChannel, _ := s.UserChannelCreate(member.User.ID)
					s.ChannelMessageSend(dmChannel.ID, "You have been verified")
					s.ChannelMessageSend(discordLogChannelID, member.User.Username+" has been verified with '"+verifiedUsers[a])
				}
			}
		}
	}
}

func handleVerifyDMs(s *discordgo.Session, m *discordgo.MessageCreate) {
	if m.Author.ID == s.State.User.ID {
		return
	}
	if strings.EqualFold(m.Content, "!verify") {
		if isVerified(m.Author.ID) {
			if m.Message.GuildID == "" {
				s.ChannelMessageSend(m.Message.ChannelID, "You are already verified")
			} else {
				dmChannel, _ := s.UserChannelCreate(m.Author.ID)
				s.ChannelMessageSend(dmChannel.ID, "You are already verified")
			}
			return
		}
		if m.Message.GuildID == "" {
			verifyToken := randomAuthKey(discordAuthTokenSize)
			verifyDiscord(m.Author.ID, verifyToken)
			s.ChannelMessageSend(m.Message.ChannelID, "Your Code is: "+verifyToken)
			s.ChannelMessageSend(m.Message.ChannelID, "In-game type /verify <code>")

		} else {
			dmChannel, _ := s.UserChannelCreate(m.Author.ID)
			verifyToken := randomAuthKey(discordAuthTokenSize)
			verifyDiscord(m.Author.ID, verifyToken)
			s.ChannelMessageSend(dmChannel.ID, "Your Code is: '"+verifyToken+"'\nIn-game type /verify <code>")
			err := s.MessageReactionAdd(m.Message.ChannelID, m.Message.ID, "🔑")
			fmt.Println(err.Error())
		}
	}
}

var playerFileQueue []string
var spawnQueue []string
var kickQueue []string

func handleCommands(s *discordgo.Session, m *discordgo.MessageCreate) {
	if m.Author.ID == s.State.User.ID {
		return
	}
	if strings.HasPrefix(m.Content, "!") && !strings.EqualFold(m.Content, "!verify") {
		if isVerified(m.Author.ID) {
			if strings.EqualFold(m.Content, "!deletePlayerFile") {
				playerFileQueue = append(playerFileQueue, m.Author.ID)
				s.ChannelMessageSend(m.Message.ChannelID, "Please specify one of the following servers: \n"+strings.Join(getServerList(), "\n"))
			} else if strings.EqualFold(m.Content, "!sendToSpawn") {
				spawnQueue = append(spawnQueue, m.Author.ID)
				s.ChannelMessageSend(m.Message.ChannelID, "Please specify one of the following servers: \n"+strings.Join(getServerList(), "\n"))
			} else if strings.EqualFold(m.Content, "!kick") {
				kickQueue = append(kickQueue, m.Author.ID)
				s.ChannelMessageSend(m.Message.ChannelID, "Please specify one of the following servers: \n"+strings.Join(getServerList(), "\n"))
			}
		} else {
			s.ChannelMessageSend(m.Message.ChannelID, "To run commands you must be verified!\nType !verify and follow its instructions to verify")
		}
	}
}

func handleServerInputCommands(s *discordgo.Session, m *discordgo.MessageCreate) {
	if m.Author.ID == s.State.User.ID {
		return
	}
	if isVerified(m.Author.ID) {
		if contains(getServerList(), m.Content) {
			uuid := getVerifiedUUID(m.Author.ID)
			if contains(playerFileQueue, m.Author.ID) {
				playerFileQueue = remove(playerFileQueue, m.Author.ID)
				sendCommand(m.Content, "deletePlayerFile "+uuid, uuid, s)
				s.ChannelMessageSend(m.Message.ChannelID, "Your Player-File has been queued to be deleted!\n Please allow up to 90s for this action to take place.")

			} else if contains(spawnQueue, m.Author.ID) {
				spawnQueue = remove(spawnQueue, m.Author.ID)
				sendCommand(m.Content, "sendToSpawn "+uuid, uuid, s)
				s.ChannelMessageSend(m.Message.ChannelID, "You will be sent to spawn!\n Please allow up to 90s for this action to take place.")
			} else if contains(kickQueue, m.Author.ID) {
				kickQueue = remove(kickQueue, m.Author.ID)
				sendCommand(m.Content, "kick "+uuid, uuid, s)
				s.ChannelMessageSend(m.Message.ChannelID, "Your will be kicked!\n Please allow up to 90s for this action to take place.")
			}
		} else {
			s.ChannelMessageSend(m.Message.ChannelID, m.Message.Content+" is not a valid server!")
		}
	}
}

func getServerList() []string {
	var servers []string
	for entry := range redisDBStatus.Keys(ctx, "*").Val() {
		var serverStatus ServerStatus
		json.Unmarshal([]byte(redisDBStatus.Get(ctx, redisDBStatus.Keys(ctx, "*").Val()[entry]).Val()), &serverStatus)
		servers = append(servers, serverStatus.ServerID)
	}
	return servers
}

func contains(arr []string, test string) bool {
	for _, entry := range arr {
		if strings.EqualFold(entry, test) {
			return true
		}
	}
	return false
}

func removeIndex(s []string, index int) []string {
	return append(s[:index], s[index+1:]...)
}

func remove(s []string, key string) []string {
	for x := 0; x < len(s); x++ {
		if strings.EqualFold(s[x], key) {
			return removeIndex(s, x)
		}
	}
	return s
}

func populateVerifiedList() {
	verifiedUsers = map[string]string{}
	for entry := range redisDBuser.Keys(ctx, "*").Val() {
		var globalUser GlobalUser
		json.Unmarshal([]byte(redisDBuser.Get(ctx, redisDBuser.Keys(ctx, "*").Val()[entry]).Val()), &globalUser)
		if len(globalUser.DiscordID) > 0 {
			verifiedUsers[globalUser.DiscordID] = globalUser.UUID
		}
	}
}

func isVerified(discordID string) bool {
	user := getVerifiedUUID(discordID)
	if len(user) > 0 {
		return true
	}
	return false
}

func getVerifiedUUID(discordID string) string {
	if len(verifiedUsers) == 0 {
		populateVerifiedList()
	}
	for a := range verifiedUsers {
		if discordID == a {
			return verifiedUsers[discordID]
		}
	}
	return ""
}

func verifyDiscord(discord string, key string) {
	token := Token{
		DiscordID: discord,
		Token:     key,
	}
	output, err := json.MarshalIndent(token, "", " ")
	if err != nil {
		return
	}
	redisDBdiscord.Set(ctx, token.DiscordID, output, 6e+11)
}

func sendCommand(serverID string, command string, player string, s *discordgo.Session) {
	newCommand := CommandRequest{
		ServerID:       serverID,
		Command:        command,
		RequiredPlayer: player,
	}
	for entry := range redisDBCommand.Keys(ctx, "*").Val() {
		var serverCommandQueue CommandQueue
		json.Unmarshal([]byte(redisDBCommand.Get(ctx, redisDBCommand.Keys(ctx, "*").Val()[entry]).Val()), &serverCommandQueue)
		if len(serverCommandQueue.Commands) < 0 && strings.EqualFold(serverCommandQueue.Commands[0].ServerID, serverID) {
			serverCommandQueue.Commands = append(serverCommandQueue.Commands, newCommand)
			output, _ := json.MarshalIndent(serverCommandQueue, "", " ")
			redisDBCommand.Set(ctx, serverCommandQueue.Commands[0].ServerID, output, 6e+11)
			s.ChannelMessageSend(discordLogChannelID, player+" has requested '"+serverID+"' run "+command)
			return
		}
	}
}
