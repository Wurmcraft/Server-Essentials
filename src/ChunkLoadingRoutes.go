package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisChunkLoadingDB *redis.Client

const permChunkLoading = "chunkloading"

func init() {
	redisChunkLoadingDB = newClient(redisDatabaseChunkLoading)
}

func GetChunkLoadingForServerID(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	serverID := string(p[0].Value)
	if redisChunkLoadingDB.Exists(ctx, serverID).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisChunkLoadingDB.Get(ctx, serverID).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func UpdateServerID(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permChunkLoading) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
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
	redisChunkLoadingDB.Set(ctx, chunkLoadingData.ServerID, output, 0)
}

func GetAllServerChunkLoading(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []ServerChunkData
	for entry := range redisChunkLoadingDB.Keys(ctx, "*").Val() {
		var serverChunkLoading ServerChunkData
		json.Unmarshal([]byte(redisChunkLoadingDB.Get(ctx, redisChunkLoadingDB.Keys(ctx, "*").Val()[entry]).Val()), &serverChunkLoading)
		data = append(data, serverChunkLoading)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}
