package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBRank *redis.Client

func init() {
	redisDBRank = newClient(redisDatabaseRank)
}

func GetRank(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	name := string(p[0].Value)
	if redisDBRank.Exists(name).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBRank.Get(name).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func SetRank(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var rank Rank
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
	redisDBRank.Set(rank.Name, output, 0)
	w.WriteHeader(http.StatusCreated)
}

func DelRank(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var rank Rank
	err = json.Unmarshal(b, &rank)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBRank.Del(rank.Name)
	w.WriteHeader(http.StatusOK)
}

func GetAllRanks(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []Rank
	for entry := range redisDBRank.Keys("*").Val() {
		var rank Rank
		json.Unmarshal([]byte(redisDBRank.Get(redisDBRank.Keys("*").Val()[entry]).Val()), &rank)
		data = append(data, rank)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}
