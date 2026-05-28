import React from 'react';
import { ShieldAlert, AlertTriangle, MapPin, Activity, CheckCircle2 } from 'lucide-react';

export const FraudWorkspace: React.FC = () => {
  return (
    <div className="fraud-grid">
      {/* Risk Gauge Card */}
      <div className="bento-card risk-gauge-box">
        <ShieldAlert size={36} color="var(--status-danger)" />
        <div className="risk-score-number" style={{ color: 'var(--status-danger)' }}>
          88
        </div>
        <div className="risk-score-label" style={{ color: 'var(--status-danger)' }}>
          High Risk Alert
        </div>
        <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 8 }}>
          Threshold: &gt;70 Triggers Automatic Investigation
        </div>

        <div style={{ width: '100%', marginTop: 24, display: 'flex', flexDirection: 'column', gap: 10 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12 }}>
            <span style={{ color: 'var(--text-secondary)' }}>Amount Spike Multiplier</span>
            <span className="font-mono" style={{ color: 'var(--status-danger)', fontWeight: 600 }}>+35 pts</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12 }}>
            <span style={{ color: 'var(--text-secondary)' }}>Rapid 5-Min Velocity</span>
            <span className="font-mono" style={{ color: 'var(--status-danger)', fontWeight: 600 }}>+30 pts</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12 }}>
            <span style={{ color: 'var(--text-secondary)' }}>Foreign IP Anomaly</span>
            <span className="font-mono" style={{ color: 'var(--status-warning)', fontWeight: 600 }}>+23 pts</span>
          </div>
        </div>
      </div>

      {/* Live Signals & Anomaly Stream */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        <div className="bento-card">
          <div className="bento-card-header">
            <div>
              <div className="bento-card-title">
                <Activity size={16} />
                Live Anomaly Telemetry Stream
              </div>
              <div className="bento-card-subtitle">Real-time risk scoring across incoming authorizations</div>
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <div
              style={{
                padding: 14,
                backgroundColor: 'var(--status-danger-bg)',
                border: '1px solid var(--status-danger-border)',
                borderRadius: 'var(--radius-md)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <AlertTriangle size={18} color="var(--status-danger)" />
                <div>
                  <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-primary)' }}>
                    Velocity Burst on Card **** 8293
                  </div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                    5 transactions attempted within 120 seconds in Mumbai
                  </div>
                </div>
              </div>
              <span className="status-pill danger">AUTO ESCALATED</span>
            </div>

            <div
              style={{
                padding: 14,
                backgroundColor: 'var(--status-warning-bg)',
                border: '1px solid var(--status-warning-border)',
                borderRadius: 'var(--radius-md)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <MapPin size={18} color="var(--status-warning)" />
                <div>
                  <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-primary)' }}>
                    Cross-Border Geolocation Mismatch
                  </div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                    Customer registered in Bengaluru; Swipe originated from London IP
                  </div>
                </div>
              </div>
              <span className="status-pill warning">REVIEW FLAG</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
