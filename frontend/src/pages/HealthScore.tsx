import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import { Heart, TrendingUp, TrendingDown, AlertCircle } from 'lucide-react';
import axios from 'axios';

export function HealthScore() {
  const { t } = useTranslation();
  const [score, setScore] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadHealthScore();
  }, []);

  async function loadHealthScore() {
    try {
      const response = await axios.get('http://localhost:8080/api/features/health-score');
      setScore(response.data.data);
    } catch (error) {
      console.error('Error loading health score:', error);
    } finally {
      setLoading(false);
    }
  }

  const getScoreColor = (score: number) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 60) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreLabel = (score: number) => {
    if (score >= 80) return t('healthScore.excellent');
    if (score >= 60) return t('healthScore.good');
    if (score >= 40) return t('healthScore.fair');
    return t('healthScore.poor');
  };

  if (loading) return <Layout><div className="flex justify-center items-center h-64">{t('common.loading')}</div></Layout>;

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">{t('healthScore.title')}</h1>

        <Card className="p-8 text-center">
          <Heart size={64} className={`mx-auto mb-4 ${getScoreColor(score?.score || 0)}`} />
          <h2 className={`text-6xl font-bold mb-2 ${getScoreColor(score?.score || 0)}`}>
            {score?.score || 0}
          </h2>
          <p className="text-xl text-gray-600 dark:text-gray-400 mb-4">
            {getScoreLabel(score?.score || 0)}
          </p>
          <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-4">
            <div
              className={`h-4 rounded-full transition-all ${
                score?.score >= 80 ? 'bg-green-500' : score?.score >= 60 ? 'bg-yellow-500' : 'bg-red-500'
              }`}
              style={{ width: `${score?.score || 0}%` }}
            />
          </div>
        </Card>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="p-6">
            <div className="flex items-center gap-3 mb-2">
              <TrendingUp className="text-green-600" size={24} />
              <h3 className="font-semibold text-gray-900 dark:text-white">{t('healthScore.savingsRate')}</h3>
            </div>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">
              {score?.savingsRate?.toFixed(1) || 0}%
            </p>
          </Card>

          <Card className="p-6">
            <div className="flex items-center gap-3 mb-2">
              <TrendingDown className="text-blue-600" size={24} />
              <h3 className="font-semibold text-gray-900 dark:text-white">{t('healthScore.expenseRatio')}</h3>
            </div>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">
              {score?.expenseRatio?.toFixed(1) || 0}%
            </p>
          </Card>

          <Card className="p-6">
            <div className="flex items-center gap-3 mb-2">
              <AlertCircle className="text-purple-600" size={24} />
              <h3 className="font-semibold text-gray-900 dark:text-white">{t('healthScore.budgetAdherence')}</h3>
            </div>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">
              {score?.budgetAdherence?.toFixed(1) || 0}%
            </p>
          </Card>
        </div>

        <Card className="p-6">
          <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">{t('healthScore.recommendations')}</h3>
          <ul className="space-y-2">
            {score?.recommendations?.map((rec: string, index: number) => (
              <li key={index} className="flex items-start gap-2 text-gray-700 dark:text-gray-300">
                <span className="text-blue-600 mt-1">•</span>
                <span>{rec}</span>
              </li>
            ))}
          </ul>
        </Card>
      </div>
    </Layout>
  );
}
