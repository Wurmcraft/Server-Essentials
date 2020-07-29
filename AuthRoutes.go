package main

import (
	b64 "encoding/base64"
	"encoding/json"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"math/rand"
	"net/http"
)

func AddAuth(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var auth Auth
	err = json.Unmarshal(b, &auth)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBAuth.Set(auth.Username, b64.StdEncoding.EncodeToString([]byte(auth.Password)), 0)
	w.WriteHeader(http.StatusCreated)
}

func DelAuth(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var auth Auth
	err = json.Unmarshal(b, &auth)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBAuth.Del(auth.Username)
	w.WriteHeader(http.StatusCreated)
}

func CreateDefaultPassword() string {
	return rString(rand.Intn(64))
}

func rString(n int) string {
	var letter = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
	b := make([]rune, n)
	for i := range b {
		b[i] = letter[rand.Intn(len(letter))]
	}
	return string(b)
}
