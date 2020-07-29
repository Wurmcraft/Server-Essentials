package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisChunkLoadingDB *redis.Client

func init() {
	redisChunkLoadingDB = newClient(redisDatabaseChunkLoading)
}

func GetChunkLoadingForServerID(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	serverID := string(p[0].Value)
	if redisChunkLoadingDB.Exists(serverID).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisChunkLoadingDB.Get(serverID).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func UpdateServerID(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	c, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var chunkLoadingData ServerChunkData
	err = json.Unmarshal(c, &chunkLoadingData)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.Marshal(chunkLoadingData)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisChunkLoadingDB.Set(chunkLoadingData.ServerID, output, 0)
}

func GetAllServerChunkLoading(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []ServerChunkData
	for entry := range redisChunkLoadingDB.Keys("*").Val() {
		var serverChunkLoading ServerChunkData
		json.Unmarshal([]byte(redisChunkLoadingDB.Get(redisChunkLoadingDB.Keys("*").Val()[entry]).Val()), &serverChunkLoading)
		data = append(data, serverChunkLoading)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}
