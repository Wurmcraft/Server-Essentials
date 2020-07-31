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
	redisDBAuth.Set(ctx, auth.Username, b64.StdEncoding.EncodeToString([]byte(auth.Password)), 0)
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
	redisDBAuth.Del(ctx, auth.Username)
	w.WriteHeader(http.StatusCreated)
}

func CreateDefaultPassword() string {
	return randSeq(16)
}

var letters = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789")

// https://stackoverflow.com/questions/22892120/how-to-generate-a-random-string-of-a-fixed-length-in-go
// All credit to Paul Hankin on stackoverflow
func randSeq(n int) string {
	b := make([]rune, n)
	for i := range b {
		b[i] = letters[rand.Intn(len(letters))]
	}
	return string(b)
}
