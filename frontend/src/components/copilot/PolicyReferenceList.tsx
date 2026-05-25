import React from 'react';
import { BookOpen, ExternalLink } from 'lucide-react';
import { PolicyReference } from '../../types/incident';

interface PolicyReferenceListProps {
  citedPolicies: PolicyReference[];
}

export const PolicyReferenceList: React.FC<PolicyReferenceListProps> = ({ citedPolicies }) => {
  if (!citedPolicies || citedPolicies.length === 0) return null;

  return (
    <div className="bento-card">
      <div className="bento-card-header">
        <div>
          <div className="bento-card-title">
            <BookOpen size={16} />
            Cited Operating Policies
          </div>
          <div className="bento-card-subtitle">
            Matched rules from the bank operational knowledge base
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        {citedPolicies.map((pol, idx) => (
          <div
            key={idx}
            style={{
              padding: 12,
              backgroundColor: 'var(--bg-surface-elevated)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
            }}
          >
            <div
              style={{
                fontSize: 12,
                fontWeight: 600,
                color: 'var(--accent-primary)',
                marginBottom: 4,
                display: 'flex',
                alignItems: 'center',
                gap: 6,
              }}
            >
              <BookOpen size={13} />
              {pol.documentName.replace('.md', '').replace(/-/g, ' ').toUpperCase()}
            </div>
            <div style={{ fontSize: 13, color: 'var(--text-secondary)' }}>
              {pol.sectionClause}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
