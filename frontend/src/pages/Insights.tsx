import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import { LoadingSpinner } from '../components/Loading';
import { TrendingUp, Download, BarChart3 } from 'lucide-react';
import axios from 'axios';

export function Insights() {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [pattern, setPattern] = useState<any>(null);
  const [forecast, setForecast] = useState<any>(null);

  useEffect(() => {
    if (user) loadData();
  }, [user]);

  async function loadData() {
    try {
      const [patternRes, forecastRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/advanced/spending-pattern/${user?.id}`),
        axios.get(`http://localhost:8080/api/advanced/forecast/${user?.id}`)
      ]);
      setPattern(patternRes.data.data);
      setForecast(forecastRes.data.data);
    } catch (error) {
      console.error('Error loading insights:', error);
    } finally {
      setLoading(false);
    }
  }

  async function exportCSV() {
    const startDate = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0];
    const endDate = new Date().toISOString().split('T')[0];
    
    try {
      const response = await axios.get(
        `http://localhost:8080/api/advanced/export/csv/${user?.id}?startDate=${startDate}&endDate=${endDate}`,
        { responseType: 'blob' }
      );
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'transactions.csv');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error exporting CSV:', error);
    }
  }

  if (loading) return <Layout><div className="flex justify-center items-center h-64"><LoadingSpinner size="lg" /></div></Layout>;

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">{t('insights.title')}</h1>
          <button
            onClick={exportCSV}
            className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg transition-colors"
          >
            <Download size={20} />
            {t('insights.exportCSV')}
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="p-6">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('insights.topCategory')}</p>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white">{pattern?.topCategory || t('common.noData')}</h3>
              </div>
              <div className="p-3 rounded-xl bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400">
                <BarChart3 size={24} />
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('insights.avgDailySpending')}</p>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white">
                  ₹{pattern?.avgDailySpending?.toFixed(2) || '0.00'}
                </h3>
              </div>
              <div className="p-3 rounded-xl bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400">
                <TrendingUp size={24} />
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('insights.projectedMonthly')}</p>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white">
                  ₹{forecast?.projectedMonthlyExpense?.toFixed(2) || '0.00'}
                </h3>
              </div>
              <div className="p-3 rounded-xl bg-orange-50 dark:bg-orange-900/20 text-orange-600 dark:text-orange-400">
                <TrendingUp size={24} />
              </div>
            </div>
          </Card>
        </div>

        <Card className="p-6">
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">{t('insights.categoryBreakdown')}</h2>
          <div className="space-y-3">
            {pattern?.categorySpending && Object.entries(pattern.categorySpending).map(([category, amount]: [string, any]) => (
              <div key={category} className="flex items-center justify-between">
                <span className="text-gray-700 dark:text-gray-300">{category}</span>
                <span className="font-semibold text-gray-900 dark:text-white">₹{amount.toFixed(2)}</span>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </Layout>
  );
}
