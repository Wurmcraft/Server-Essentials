package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBtransfer *redis.Client

func init() {
	redisDBtransfer = newClient(redisDatabaseTransfer)
}

func GetTransferData(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	uuid := string(p[0].Value)
	if redisDBtransfer.Exists(uuid).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBtransfer.Get(uuid).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func SetTransferData(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	var transferData TransferBin
	err = json.Unmarshal(b, &transferData)
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.MarshalIndent(transferData, "", " ")
	if err != nil {
		fmt.Println(err.Error())
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBtransfer.Set(transferData.UUID, output, 0)
	w.WriteHeader(http.StatusCreated)
}
