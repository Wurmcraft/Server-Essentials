import React, {useState} from "react";
import { BrowserRouter as Router, Routes, Route}
  from 'react-router-dom';

import Login from './pages/Login.js'
import Home from './pages/Home.js'
import UserLookup from "./pages/UserLookup";
import Ranks from "./pages/Ranks";
import Rankup from "./pages/Rankup";
import Market from "./pages/Market";
import Bans from "./pages/Bans";
import Stats from "./pages/Stats";
import Transfers from "./pages/Transfers";
import Currency from "./pages/Currency";
import Logs from "./pages/Logs";
import Account from "./pages/Account";
import Status from "./pages/Status";

import './css/base.css';

function App() {
  const [isAuthenticated] = useState([false])
  return (
      <Router>
        <Routes>
          <Route path='/'  element={<Home/>} />
          <Route path='/login' element={<Login/>} />
          <Route path='/user-lookup' element={<UserLookup/>} />
          <Route path='/ranks' element={<Ranks/>} />
          <Route path='/rankup' element={<Rankup/>} />
          <Route path='/market' element={<Market/>} />
          <Route path='/bans' element={<Bans/>} />
          <Route path='/stats' element={<Stats/>} />
          <Route path='/transfers' element={<Transfers/>} />
          <Route path='/currency' element={<Currency/>} />
          <Route path='/logs' element={<Logs/>} />
          <Route path='/account' element={<Account/>} />
          <Route path='/status' element={<Status/>} />
        </Routes>
      </Router>
  );
}

export default App;
