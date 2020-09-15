package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBCommand *redis.Client

const permCommands = "commands"

func init() {
	redisDBCommand = newClient(redisCommandStorage)
}

func GetAllCommands(w http.ResponseWriter, _ *http.Request, m mux.Params) {
	var data []CommandQueue
	for entry := range redisDBCommand.Keys(ctx, "*").Val() {
		var serverCommandQueue CommandQueue
		json.Unmarshal([]byte(redisDBCommand.Get(ctx, redisDBCommand.Keys(ctx, "*").Val()[entry]).Val()), &serverCommandQueue)
		data = append(data, serverCommandQueue)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}

func addCommand(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if hasPermission(GetPermission(r.Header.Get("token")), permCommands) {
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
	var queue CommandQueue
	err = json.Unmarshal(b, &queue)
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.MarshalIndent(queue, "", " ")
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBCommand.Set(ctx, queue.Commands[0].ServerID, output, 6e+11)
	w.WriteHeader(http.StatusCreated)
}
