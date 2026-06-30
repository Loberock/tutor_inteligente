import { useState } from "react";
import { AuthScreen } from "./components/AuthScreen";
import { Workspace } from "./components/Dashboards";
import { readSession, SESSION_KEY } from "./services/api";
import "./App.css";

function App() {
  const [session, setSession] = useState(readSession);

  const saveSession = (nextSession) => {
    setSession(nextSession);
    localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession));
  };

  const logout = () => {
    setSession(null);
    localStorage.removeItem(SESSION_KEY);
  };

  return (
    <main className="app-shell">
      {!session ? (
        <AuthScreen onLogin={saveSession} />
      ) : (
        <Workspace session={session} onLogout={logout} />
      )}
    </main>
  );
}

export default App;
