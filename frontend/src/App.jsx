import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Signup from './pages/Signup';
import Login from './pages/Login';
import VerifyOtp from './pages/VerifyOtp';
import Home from './pages/Home';
import AddPet from './pages/AddPet';
import EditPet from './pages/EditPet';
import Chat from './pages/Chat';
import PetDetails from './pages/PetDetails';
import Inbox from './pages/Inbox';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    
    if (token && userData) {
      setIsAuthenticated(true);
      setUser(JSON.parse(userData));
    }
  }, []);

  const handleLogin = (userData) => {
    setIsAuthenticated(true);
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
    setUser(null);
  };

  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <div className="App">
        <Navbar 
          isAuthenticated={isAuthenticated} 
          user={user} 
          onLogout={handleLogout} 
        />
        <main className="app-main">
          <Routes>
            <Route 
              path="/signup" 
              element={!isAuthenticated ? <Signup /> : <Navigate to="/" />} 
            />
            <Route 
              path="/login" 
              element={!isAuthenticated ? <Login onLogin={handleLogin} /> : <Navigate to="/" />} 
            />
            <Route path="/verify-otp" element={<VerifyOtp />} />
            <Route path="/verify" element={<Navigate to="/verify-otp" />} />
            <Route 
              path="/" 
              element={isAuthenticated ? <Home user={user} /> : <Navigate to="/login" />} 
            />
            <Route
              path="/pets/:id"
              element={isAuthenticated ? <PetDetails user={user} /> : <Navigate to="/login" />}
            />
            <Route
              path="/messages"
              element={isAuthenticated ? <Inbox /> : <Navigate to="/login" />}
            />
            <Route 
              path="/add-pet" 
              element={
                isAuthenticated && (user?.role === 'COMMON_LISTER' || user?.role === 'NGO_LISTER') 
                  ? <AddPet /> 
                  : <Navigate to="/" />
              } 
            />
            <Route
              path="/edit-pet/:id"
              element={
                isAuthenticated && (user?.role === 'COMMON_LISTER' || user?.role === 'NGO_LISTER')
                  ? <EditPet user={user} />
                  : <Navigate to="/" />
              }
            />
            <Route 
              path="/chat/:userId" 
              element={isAuthenticated ? <Chat currentUser={user} /> : <Navigate to="/login" />} 
            />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
