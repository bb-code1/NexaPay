import React, { useState, useMemo } from 'react';
import { Search, Filter, AlertCircle, CheckCircle2, Clock } from 'lucide-react';
import { ScenarioPreset } from '../../types/incident';
import { SCENARIO_PRESETS } from '../../data/scenarios';

interface IncidentFeedProps {
  selectedScenario: ScenarioPreset | null;
  onSelectScenario: (scenario: ScenarioPreset) => void;
}

type FilterCategory = 'ALL' | 'CARD_LIMIT' | 'FRAUD_SIGNAL' | 'SETTLEMENT' | 'LIFECYCLE' | 'AUTHORIZATION';

export const IncidentFeed: React.FC<IncidentFeedProps> = ({
  selectedScenario,
  onSelectScenario,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [category, setCategory] = useState<FilterCategory>('ALL');

  const filteredScenarios = useMemo(() => {
    return SCENARIO_PRESETS.filter((s) => {
      const matchesCategory = category === 'ALL' || s.category === category;
      const matchesSearch =
        s.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        s.entityRef.toLowerCase().includes(searchQuery.toLowerCase()) ||
        s.query.toLowerCase().includes(searchQuery.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [searchQuery, category]);

  const categories: { label: string; value: FilterCategory }[] = [
    { label: 'All (20)', value: 'ALL' },
    { label: 'Card Limits', value: 'CARD_LIMIT' },
    { label: 'Fraud Alerts', value: 'FRAUD_SIGNAL' },
    { label: 'Settlement', value: 'SETTLEMENT' },
    { label: 'Lifecycle', value: 'LIFECYCLE' },
    { label: 'Auth Rules', value: 'AUTHORIZATION' },
  ];

  return (
    <aside className="bento-card incident-feed-card">
      <div className="bento-card-header">
        <div>
          <div className="bento-card-title">
            <Filter size={16} />
            Incident Feed
          </div>
          <div className="bento-card-subtitle">Select a scenario to investigate</div>
        </div>
      </div>

      <input
        type="text"
        className="input-search"
        placeholder="Search incident ref or title..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
      />

      <div className="incident-filter-pills">
        {categories.map((cat) => (
          <button
            key={cat.value}
            className={`filter-pill ${category === cat.value ? 'active' : ''}`}
            onClick={() => setCategory(cat.value)}
          >
            {cat.label}
          </button>
        ))}
      </div>

      <div className="scenario-list">
        {filteredScenarios.map((s) => {
          const isSelected = selectedScenario?.id === s.id;
          return (
            <div
              key={s.id}
              className={`scenario-item ${isSelected ? 'selected' : ''}`}
              onClick={() => onSelectScenario(s)}
            >
              <div className="scenario-ref">{s.entityRef}</div>
              <div className="scenario-title">{s.title}</div>
            </div>
          );
        })}
      </div>
    </aside>
  );
};
