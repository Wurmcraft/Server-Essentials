package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	"io/ioutil"
	"net/http"
)

var redisDBdiscord *redis.Client

const permDiscord = "discord"

func init() {
	redisDBdiscord = newClient(redisDatabaseDiscord)
}

func SetToken(w http.ResponseWriter, r *http.Request) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permDiscord) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	var token Token
	err = json.Unmarshal(b, &token)
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.MarshalIndent(token, "", " ")
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBdiscord.Set(ctx, token.DiscordID, output, 6e+11)
	w.WriteHeader(http.StatusCreated)
}

func GetAllTokens(w http.ResponseWriter, _ *http.Request) {
	var data []Token
	for entry := range redisDBdiscord.Keys(ctx, "*").Val() {
		var discordToken Token
		json.Unmarshal([]byte(redisDBdiscord.Get(ctx, redisDBdiscord.Keys(ctx, "*").Val()[entry]).Val()), &discordToken)
		data = append(data, discordToken)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}