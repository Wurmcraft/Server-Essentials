package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBUUID *redis.Client

const permUUID = "uuid"

func init() {
	redisDBUUID = newClient(redisDatabaseUUID)
}

func SetUUID(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permUUID) {
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
	var uuidLookup NameLookup
	err = json.Unmarshal(b, &uuidLookup)
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.MarshalIndent(uuidLookup, "", " ")
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBUUID.Set(ctx, uuidLookup.UUID, output, 0)
	w.WriteHeader(http.StatusCreated)
}

func GetUserUUID(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	uuid := string(p[0].Value)
	if redisDBuser.Exists(ctx, uuid).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBUUID.Get(ctx, uuid).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func GetAllNames(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permUUID) {
		var data []NameLookup
		for entry := range redisDBUUID.Keys(ctx, "*").Val() {
			var name NameLookup
			json.Unmarshal([]byte(redisDBUUID.Get(ctx, redisDBUUID.Keys(ctx, "*").Val()[entry]).Val()), &name)
			data = append(data, name)
		}
		output, err := json.MarshalIndent(data, " ", " ")
		if err != nil {
			fmt.Fprintln(w, "{}")
			return
		}
		fmt.Fprintln(w, string(output))
	} else {
		var data []UserBasic
		for entry := range redisDBuser.Keys(ctx, "*").Val() {
			var globalUser GlobalUser
			json.Unmarshal([]byte(redisDBuser.Get(ctx, redisDBuser.Keys(ctx, "*").Val()[entry]).Val()), &globalUser)
			data = append(data, UserBasic{
				UUID: globalUser.UUID,
				Rank: globalUser.Rank,
			})
		}
		playerData := AllPlayersBasic{Players: data}
		output, err := json.MarshalIndent(playerData, " ", " ")
		if err != nil {
			fmt.Fprintln(w, "{}")
			return
		}
		fmt.Fprintln(w, string(output))
	}
}
