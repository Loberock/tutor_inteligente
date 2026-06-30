import { LogOut } from "lucide-react";
import { CLASSROOM_IMAGE_SRC } from "../config/assets";

export function AppHeader({ session, onLogout }) {
  return (
    <header className="app-header">
      <div className="product-title">
        <img className="brand-logo" src={CLASSROOM_IMAGE_SRC} alt="" />
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
