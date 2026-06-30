import { CheckCircle2 } from "lucide-react";

export function Panel({ icon, title, aside, children, className = "" }) {
  return (
    <section className={`panel ${className}`.trim()}>
      <header className="panel-header">
        <div>
          {icon}
          <h2>{title}</h2>
        </div>
        {aside && <span>{aside}</span>}
      </header>
      {children}
    </section>
  );
}

export function Field({ label, value, onChange, type = "text", placeholder = "" }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} placeholder={placeholder} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

export function Select({ label, value, onChange, children }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select value={value} onChange={(event) => onChange(event.target.value)}>
        {children}
      </select>
    </label>
  );
}

export function SegmentedControl({ value, options, onChange }) {
  return (
    <div className="segmented-control">
      {options.map((option) => (
        <button key={option.value} className={value === option.value ? "active" : ""} onClick={() => onChange(option.value)}>
          {option.label}
        </button>
      ))}
    </div>
  );
}

export function SubmitButton({ icon, label }) {
  return (
    <button className="primary-button">
      {icon}
      {label}
    </button>
  );
}

export function StatusMessage({ status }) {
  if (!status.message) return null;
  return <p className={`status ${status.type}`}>{status.message}</p>;
}

export function Stat({ label, value }) {
  return (
    <div className="stat">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

export function Pill({ children }) {
  return <span className="pill">{children}</span>;
}

export function EmptyState({ title, text }) {
  return (
    <div className="empty-state">
      <CheckCircle2 size={18} />
      <strong>{title}</strong>
      <span>{text}</span>
    </div>
  );
}
