import React, { useState } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Navbar } from './components/layout/Navbar';
import { Toast } from './components/layout/Toast';
import { CopilotWorkspace } from './components/copilot/CopilotWorkspace';
import { LedgerWorkspace } from './components/ledger/LedgerWorkspace';
import { FraudWorkspace } from './components/fraud/FraudWorkspace';
import { BenchmarkWorkspace } from './components/benchmark/BenchmarkWorkspace';
import { useAuth } from './hooks/useAuth';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60 * 1000,
      refetchOnWindowFocus: false,
    },
  },
});

export const AppContent: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'copilot' | 'ledger' | 'fraud' | 'benchmark'>('copilot');
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const { currentPersona, setCurrentPersona, personas } = useAuth();

  const handleShowToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 4000);
  };

  return (
    <div className="app-container">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currentPersona={currentPersona}
        setCurrentPersona={setCurrentPersona}
        personas={personas}
      />

      <div className="main-content">
        {activeTab === 'copilot' && <CopilotWorkspace onShowToast={handleShowToast} />}
        {activeTab === 'ledger' && <LedgerWorkspace />}
        {activeTab === 'fraud' && <FraudWorkspace />}
        {activeTab === 'benchmark' && <BenchmarkWorkspace />}
      </div>

      <Toast message={toastMessage} onClose={() => setToastMessage(null)} />
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AppContent />
    </QueryClientProvider>
  );
};

export default App;
