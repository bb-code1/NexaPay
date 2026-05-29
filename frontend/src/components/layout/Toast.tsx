import React from 'react';
import { CheckCircle2, X } from 'lucide-react';

interface ToastProps {
  message: string | null;
  onClose: () => void;
}

export const Toast: React.FC<ToastProps> = ({ message, onClose }) => {
  if (!message) return null;

  return (
    <div
      style={{
        position: 'fixed',
        bottom: 24,
        right: 24,
        backgroundColor: 'var(--bg-surface-elevated)',
        border: '1px solid var(--status-success-border)',
        borderRadius: 'var(--radius-md)',
        padding: '14px 18px',
        display: 'flex',
        alignItems: 'center',
        gap: 12,
        boxShadow: 'var(--shadow-elevated)',
        zIndex: 200,
        maxWidth: 400,
      }}
    >
      <CheckCircle2 size={18} color="var(--status-success)" />
      <span style={{ fontSize: 13, color: 'var(--text-primary)', flex: 1 }}>{message}</span>
      <button
        onClick={onClose}
        style={{
          background: 'none',
          border: 'none',
          color: 'var(--text-muted)',
          cursor: 'pointer',
          padding: 2,
        }}
      >
        <X size={15} />
      </button>
    </div>
  );
};
