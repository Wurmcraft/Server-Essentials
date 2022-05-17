import React from 'react'

import './css/navbar.css';

export default function Home() {
  return (
      <div>
        <ul>
          <li><a href="/">Home</a> </li>
          <li><a href="/user-lookup">User Lookup</a> </li>
          <li><a href="/ranks">Ranks</a> </li>
          <li><a href="/rankup">Rankup</a> </li>
          <li><a href="/market">Market</a> </li>
          <li><a href="/bans">Bans</a> </li>
          <li><a href="/stats">Statistics</a> </li>
          <li><a href="/transfers">Transfers</a> </li>
          <li><a href="/currency">Currency</a> </li>
          <li><a href="/logs">Logs</a> </li>
          <li><a href="/account">Account</a> </li>
          <li><a href="/status">Status</a> </li>
        </ul>
      </div>
  )
}
