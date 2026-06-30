import { LogOut } from "lucide-react";

export function AppHeader({ session, onLogout }) {
  return (
    <header className="app-header">
      <div className="product-title">
        <span className="brand-emoji" aria-hidden="true">🧠</span>
        <strong>Tutor Inteligente</strong>
      </div>
      {session && (
        <button className="icon-button" onClick={onLogout} title="Cerrar sesion">
          <LogOut size={17} />
          Cerrar sesion
        </button>
      )}
    </header>
  );
}
