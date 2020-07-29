package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBAutoRank *redis.Client

func init() {
	redisDBAutoRank = newClient(redisDatabaseAutoRank)
}

func GetAutoRank(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	name := string(p[0].Value)
	fmt.Println(name + " " + string(redisDBAutoRank.Exists(name).Val()))
	if redisDBAutoRank.Exists(name).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBAutoRank.Get(name).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func SetAutoRank(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var rank AutoRank
	err = json.Unmarshal(b, &rank)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.Marshal(rank)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBAutoRank.Set(rank.Rank, output, 0)
	w.WriteHeader(http.StatusCreated)
}

func DelAutoRank(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var rank AutoRank
	err = json.Unmarshal(b, &rank)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBAutoRank.Del(rank.Rank)
	w.WriteHeader(http.StatusCreated)
}

func GetAllAutoRanks(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []AutoRank
	for entry := range redisDBAutoRank.Keys("*").Val() {
		var rank AutoRank
		json.Unmarshal([]byte(redisDBAutoRank.Get(redisDBAutoRank.Keys("*").Val()[entry]).Val()), &rank)
		data = append(data, rank)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}
