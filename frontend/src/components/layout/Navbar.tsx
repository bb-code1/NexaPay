import React from 'react';
import { Layers, Bot, ShieldAlert, BarChart3, ExternalLink } from 'lucide-react';
import { AuthPersona } from '../../types/auth';

interface NavbarProps {
  activeTab: 'copilot' | 'ledger' | 'fraud' | 'benchmark';
  setActiveTab: (tab: 'copilot' | 'ledger' | 'fraud' | 'benchmark') => void;
  currentPersona: AuthPersona;
  setCurrentPersona: (persona: AuthPersona) => void;
  personas: AuthPersona[];
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  setActiveTab,
  currentPersona,
  setCurrentPersona,
  personas,
}) => {
  return (
    <header className="navbar">
      <div className="nav-brand">
        <div className="nav-logo-box">
          <Layers size={18} />
        </div>
        <div>
          <div className="nav-title">NexaPay</div>
          <div className="nav-subtitle">Payment Operations & Autonomous Investigation Center</div>
        </div>
      </div>

      <nav className="nav-tabs">
        <button
          className={`nav-tab ${activeTab === 'copilot' ? 'active' : ''}`}
          onClick={() => setActiveTab('copilot')}
        >
          <Bot size={15} />
          Incident Copilot
        </button>

        <button
          className={`nav-tab ${activeTab === 'ledger' ? 'active' : ''}`}
          onClick={() => setActiveTab('ledger')}
        >
          <Layers size={15} />
          Account Ledger
        </button>

        <button
          className={`nav-tab ${activeTab === 'fraud' ? 'active' : ''}`}
          onClick={() => setActiveTab('fraud')}
        >
          <ShieldAlert size={15} />
          Risk & Fraud Radar
        </button>

        <button
          className={`nav-tab ${activeTab === 'benchmark' ? 'active' : ''}`}
          onClick={() => setActiveTab('benchmark')}
        >
          <BarChart3 size={15} />
          Accuracy Benchmarks
        </button>
      </nav>

      <div className="nav-actions">
        <span className="status-pill success">
          <span style={{ width: 6, height: 6, borderRadius: '50%', backgroundColor: 'var(--status-success)' }}></span>
          Operational
        </span>

        <select
          className="persona-select"
          value={currentPersona.role}
          onChange={(e) => {
            const selected = personas.find((p) => p.role === e.target.value);
            if (selected) setCurrentPersona(selected);
          }}
        >
          {personas.map((p) => (
            <option key={p.role} value={p.role}>
              {p.name} ({p.label})
            </option>
          ))}
        </select>

        <a
          href="/swagger-ui.html"
          target="_blank"
          rel="noopener noreferrer"
          className="btn btn-secondary"
          title="Open API Documentation"
        >
          API Docs
          <ExternalLink size={13} />
        </a>
      </div>
    </header>
  );
};
