package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	"github.com/gorilla/mux"
	"io/ioutil"
	"net/http"
)

var redisDBBan *redis.Client

const permBan = "ban"

func init() {
	redisDBBan = newClient(redisDatabaseBan)
}

func GetBan(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	name := vars["uuid"]
	if redisDBBan.Exists(ctx, name).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBBan.Get(ctx, name).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func CreateBan(w http.ResponseWriter, r *http.Request) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permBan) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var ban Ban
	err = json.Unmarshal(b, &ban)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.Marshal(ban)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBBan.Set(ctx, ban.UUID, output, 0)
	w.WriteHeader(http.StatusCreated)
}

func GetAllBans(w http.ResponseWriter, _ *http.Request) {
	var data []Ban
	for entry := range redisDBBan.Keys(ctx, "*").Val() {
		var ban Ban
		json.Unmarshal([]byte(redisDBBan.Get(ctx, redisDBBan.Keys(ctx, "*").Val()[entry]).Val()), &ban)
		data = append(data, ban)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}

func DeleteBan(w http.ResponseWriter, r *http.Request) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permBan) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var ban Ban
	err = json.Unmarshal(b, &ban)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBBan.Del(ctx, ban.UUID)
	w.WriteHeader(http.StatusCreated)
}
