package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"net/http"
)

var redisDBEco *redis.Client
var redisDBclient *redis.Client

func init() {
	redisDBEco = newClient(redisDatabaseEco)
	redisDBclient = newClient(redisDatabaseUser)
}

func GetEco(w http.ResponseWriter, _ *http.Request, p mux.Params) {
	name := string(p[0].Value)
	if redisDBEco.Exists(ctx, name).Val() == 1 {
		w.Header().Set("content-type", "application/json")
		w.Header().Set("version", version)
		w.Write([]byte(redisDBEco.Get(ctx, name).Val()))
	} else {
		w.WriteHeader(http.StatusNotFound)
	}
}

func SetEco(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var currency CurrencyConvert
	err = json.Unmarshal(b, &currency)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.Marshal(currency)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBEco.Set(ctx, currency.Name, output, 0)
	w.WriteHeader(http.StatusCreated)
}

func DelEco(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var currency CurrencyConvert
	err = json.Unmarshal(b, &currency)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBEco.Del(ctx, currency.Name)
	w.WriteHeader(http.StatusCreated)
}

func GetAllEco(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []CurrencyConvert
	for entry := range redisDBEco.Keys(ctx, "*").Val() {
		var eco CurrencyConvert
		json.Unmarshal([]byte(redisDBEco.Get(ctx, redisDBEco.Keys(ctx, "*").Val()[entry]).Val()), &eco)
		data = append(data, eco)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil {
		fmt.Fprintln(w, "{}")
		return
	}
	fmt.Fprintln(w, string(output))
}
