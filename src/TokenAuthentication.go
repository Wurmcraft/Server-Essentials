package main

import (
	"encoding/json"
	"fmt"
	mux "github.com/julienschmidt/httprouter"
	"golang.org/x/crypto/bcrypt"
	"io/ioutil"
	"log"
	"math/rand"
	"net/http"
	"strings"
	"time"
)

var authTokenLength = 64

const permAuth = "auth"
const permAuthRenew = "authRenew"

func AddAuth(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permAuth) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	var auth AuthStorage
	err = json.Unmarshal(b, &auth)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	code := randomAuthKey(64)
	auth.AuthToken = hashCode(code)
	a, err := json.Marshal(auth)
	if err != nil {
		panic(err.Error())
	}
	redisDBAuth.Set(ctx, auth.UserId, a, 24*time.Hour)
	log.Println("Auth Token for '" + auth.UserId + "' created with [" + strings.Join(auth.Permission, ", ") + "]")
	w.WriteHeader(http.StatusCreated)
	w.Write([]byte("{\n token:\"" + code + "\"}"))
}

func UpdateAuth(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permAuth) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	var auth AuthStorage
	err = json.Unmarshal(b, &auth)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	authStorage := AuthStorage{}
	_ = json.Unmarshal([]byte(redisDBAuth.Get(ctx, auth.UserId).Val()), &authStorage)
	auth.AuthToken = authStorage.AuthToken
	a, err := json.Marshal(auth)
	if err != nil {
		panic(err.Error())
	}
	redisDBAuth.Set(ctx, auth.UserId, a, 24*time.Hour)
	log.Println("Auth Token for '" + auth.UserId + "' updated with [" + strings.Join(auth.Permission, ", ") + "]")
	w.WriteHeader(http.StatusOK)
}

func ReAuth(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permAuthRenew) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	auth := GetPermission(r.Header.Get("token"))
	a, err := json.Marshal(auth)
	if err != nil {
		fmt.Println(err.Error())
	}
	redisDBAuth.Set(ctx, auth.UserId, a, 24*time.Hour)
}

func DelAuth(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	if !hasPermission(GetPermission(r.Header.Get("token")), permAuth) {
		http.Error(w, "Forbidden", http.StatusForbidden)
		return
	}
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	var auth AuthStorage
	err = json.Unmarshal(b, &auth)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	redisDBAuth.Del(ctx, auth.UserId)
	log.Println("Auth Token for '" + auth.UserId + " deleted!")
	w.WriteHeader(http.StatusAccepted)
}

func hashCode(code string) string {
	hash, err := bcrypt.GenerateFromPassword([]byte(code), bcrypt.DefaultCost)
	if err != nil {
		log.Fatal(err)
	}
	return string(hash)
}

func randomAuthKey(size int) string {
	rand.Seed(time.Now().UnixNano())
	chars := []rune("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
		"abcdefghijklmnopqrstuvwxyz" +
		"0123456789")
	var b strings.Builder
	for i := 0; i < size; i++ {
		b.WriteRune(chars[rand.Intn(len(chars))])
	}
	return b.String()
}

type AuthStorage struct {
	UserId     string   `json:"user_id"`
	AuthToken  string   `json:"token"`
	Permission []string `json:"permission"`
}
