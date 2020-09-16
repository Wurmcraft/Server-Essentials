package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBtransfer *redis.Client

const permTransfer = "transfer"

func init() {
	redisDBtransfer = newClient(redisDatabaseTransfer)
}

func GetTransferData(w http.ResponseWriter, r *http.Request, p mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permTransfer) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	uuid := string(p[0].Value)
	if redisDBtransfer.Exists(ctx, uuid).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBtransfer.Get(ctx, uuid).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func SetTransferData(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permTransfer) {
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
	redisDBtransfer.Set(ctx, transferData.UUID, output, 0)
	w.WriteHeader(http.StatusCreated)
}
