package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBBan *redis.Client

func init() {
	redisDBBan = newClient(redisDatabaseBan)
}

func GetBan(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	name := string(p[0].Value)
	if redisDBBan.Exists(name).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBBan.Get(name).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func CreateBan(w http.ResponseWriter, r *http.Request, _ mux.Params) {
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
	redisDBBan.Set(ban.UUID, output, 0)
	w.WriteHeader(http.StatusCreated)
}

func GetAllBans(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []Ban
	for entry := range redisDBBan.Keys("*").Val() {
		var ban Ban
		json.Unmarshal([]byte(redisDBBan.Get(redisDBBan.Keys("*").Val()[entry]).Val()), &ban)
		data = append(data, ban)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}

func DeleteBan(w http.ResponseWriter, r *http.Request, _ mux.Params) {
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
	redisDBBan.Del(ban.UUID)
	w.WriteHeader(http.StatusCreated)
}
