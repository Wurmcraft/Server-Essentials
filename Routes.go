package main

import (
	"encoding/base64"
	b64 "encoding/base64"
	"fmt"
	mux "github.com/julienschmidt/httprouter"
	"net/http"
	"strings"
)

type Route struct {
	Name        string
	Method      string
	Pattern     string
	RequireAuth bool
	Handle      mux.Handle
}

type Routes []Route

var routes = Routes{
	// User
	Route{
		"Index",
		"GET",
		"/",
		false,
		Index,
	},
	Route{
		"Validate",
		"GET",
		"/api/validate",
		true,
		Validate,
	},
	Route{
		"GetUsers",
		"GET",
		"/api/user/:uuid",
		false,
		GetGlobalUser,
	},
	Route{
		"AddUser",
		"POST",
		"/api/user/add",
		true,
		SetGlobalUser,
	},
	Route{
		"OverrideUser",
		"PUT",
		"/api/user/:uuid/override",
		true,
		SetGlobalUser,
	},
	Route{
		"AllUsers",
		"GET",
		"/api/user",
		false,
		GetAllUsers,
	},
	// Rank
	Route{
		"GetRank",
		"GET",
		"/api/rank/:name",
		false,
		GetRank,
	},
	Route{
		"SetRank",
		"POST",
		"/api/rank/add",
		true,
		SetRank,
	},
	Route{
		"OverrideRank",
		"PUT",
		"/api/rank/:name/override",
		true,
		SetRank,
	},
	Route{
		"AllRanks",
		"GET",
		"/api/rank",
		false,
		GetAllRanks,
	},
	Route{
		"DelRank",
		"Post",
		"/rank/del",
		false,
		DelRank,
	},
	// AutoRanks
	Route{
		"GetAutoRank",
		"GET",
		"/api/autoRank/:name",
		false,
		GetAutoRank,
	},
	Route{
		"SetAutoRank",
		"POST",
		"/api/autoRank/add",
		true,
		SetAutoRank,
	},
	Route{
		"OverrideAutoRank",
		"PUT",
		"/api/autoRank/:name/override",
		true,
		SetAutoRank,
	},
	Route{
		"DelAutoRank",
		"PUT",
		"/api/autoRank/:name/del",
		true,
		DelAutoRank,
	},
	Route{
		"AllAutoRanks",
		"GET",
		"/api/autoRank",
		false,
		GetAllAutoRanks,
	},
	// Eco (Currency)
	Route{
		"GetCurrency",
		"GET",
		"/api/eco/:name",
		false,
		GetEco,
	},
	Route{
		"SetCurrency",
		"POST",
		"/api/eco/add",
		true,
		SetEco,
	},
	Route{
		"OverrideCurrency",
		"PUT",
		"/api/eco/:name/override",
		true,
		SetEco,
	},
	Route{
		"DeleteEco",
		"PUT",
		"/api/eco/:name/del",
		true,
		DelEco,
	},
	Route{
		"AllCurrency",
		"GET",
		"/api/eco",
		false,
		GetAllEco,
	},
	// Status
	Route{
		"GetStatus",
		"GET",
		"/api/status",
		false,
		GetServerStatus,
	},
	Route{
		"GetStatus",
		"GET",
		"/status",
		false,
		GetStatus,
	},
	Route{
		"UpdateStatus",
		"POST",
		"/api/status",
		true,
		PostStatus,
	},
	Route{
		"GetTransfer",
		"GET",
		"/api/transfer/:uuid",
		false,
		GetTransferData,
	},
	Route{
		"AddTransfer",
		"POST",
		"/api/transfer/add",
		true,
		SetTransferData,
	},
	Route{
		"OverrideTransfer",
		"PUT",
		"/api/transfer/:uuid/override",
		true,
		SetTransferData,
	},
	Route{
		"AddToken",
		"POST",
		"/api/discord/add",
		true,
		SetToken,
	},
	Route{
		"ListToken",
		"GET",
		"/api/discord/list",
		true,
		GetAllTokens,
	},
	Route{
		"GetBan",
		"GET",
		"/api/ban/:uuid",
		false,
		GetBan,
	},
	Route{
		"AddBan",
		"POST",
		"/api/ban/add",
		true,
		CreateBan,
	},
	Route{
		"GetAllBans",
		"GET",
		"/api/ban",
		false,
		GetAllBans,
	},
	Route{
		"Unban",
		"PUT",
		"/api/ban/:uuid/del",
		true,
		DeleteBan,
	},
	Route{
		"AddAuth",
		"POST",
		"/api/auth/add",
		true,
		AddAuth,
	},
	Route{
		"DelAuth",
		"PUT",
		"/api/auth/delete",
		true,
		AddAuth,
	},
	Route{
		"AddChunkLoading",
		"POST",
		"/api/chunkloading/add",
		true,
		UpdateServerID,
	},
	Route{
		"GetChunkLoading",
		"GET",
		"/api/chunkloading/:serverID",
		false,
		GetChunkLoadingForServerID,
	},
	Route{
		"GetChunkLoading",
		"GET",
		"/api/chunkloading",
		false,
		GetAllServerChunkLoading,
	},
	Route{
		"theme.css",
		"GET",
		"/theme.css",
		false,
		GetCSS,
	},
}

func Index(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	_, _ = fmt.Fprintf(w, "Welcome to the Server-Essentials Rest API v"+version)
}

func auth(pass mux.Handle) mux.Handle {
	return func(w http.ResponseWriter, r *http.Request, m mux.Params) {
		auth := strings.SplitN(r.Header.Get("Authorization"), " ", 2)
		if len(auth) != 2 || auth[0] != "Basic" {
			http.Error(w, "Failed to Authorize", http.StatusUnauthorized)
			return
		}
		payload, _ := base64.StdEncoding.DecodeString(auth[1])
		pair := strings.SplitN(string(payload), ":", 2)
		if len(pair) != 2 || !validate(pair[0], pair[1]) {
			http.Error(w, "Failed to Authorize", http.StatusUnauthorized)
			return
		}
		pass(w, r, m)
	}
}

func validate(server, authKey string) bool {
	if redisDBAuth.Exists(ctx, server).Val() == 1 {
		auth, err := b64.StdEncoding.DecodeString(redisDBAuth.Get(ctx, server).Val())
		if err != nil {
			return false
		}
		return string(auth) == authKey
	}
	return false
}
