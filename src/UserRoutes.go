package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBuser *redis.Client

func init() {
	redisDBuser = newClient(redisDatabaseUser)
}

func GetGlobalUser(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	uuid := string(p[0].Value)
	if redisDBuser.Exists(ctx, uuid).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBuser.Get(ctx, uuid).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func SetGlobalUser(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	var globalUser GlobalUser
	err = json.Unmarshal(b, &globalUser)
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.MarshalIndent(globalUser, "", " ")
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBuser.Set(ctx, globalUser.UUID, output, 0)
	w.WriteHeader(http.StatusCreated)
}

func GetAllUsers(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []UserSimple
	for entry := range redisDBuser.Keys(ctx, "*").Val() {
		var globalUser GlobalUser
		json.Unmarshal([]byte(redisDBuser.Get(ctx, redisDBuser.Keys(ctx, "*").Val()[entry]).Val()), &globalUser)
		data = append(data, UserSimple{
			UUID:    globalUser.UUID,
			Rank:    globalUser.Rank,
			Discord: globalUser.DiscordID,
			Wallet:  globalUser.Wallet,
		})
	}
	playerData := AllPlayers{Players: data}
	output, err := json.MarshalIndent(playerData, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}
