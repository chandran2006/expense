import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { supabase } from '../services/supabase';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import { LoadingSpinner } from '../components/Loading';
import { AlertTriangle, TrendingDown, Wallet } from 'lucide-react';

interface Budget {
  id: string;
  month: string;
  amount: number;
}

export function Budget() {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [budget, setBudget] = useState<Budget | null>(null);
  const [budgetAmount, setBudgetAmount] = useState('');
  const [currentSpending, setCurrentSpending] = useState(0);
  const currentMonth = new Date().toISOString().slice(0, 7);

  useEffect(() => {
    if (user) {
      loadData();
    }
  }, [user]);

  async function loadData() {
    try {
      const { data: budgetData } = await supabase
        .from('budgets')
        .select('*')
        .eq('user_id', user?.id)
        .eq('month', currentMonth)
        .maybeSingle();

      const { data: transactionsData } = await supabase
        .from('transactions')
        .select('amount')
        .eq('user_id', user?.id)
        .eq('type', 'expense')
        .gte('date', `${currentMonth}-01`)
        .lt('date', getNextMonth(currentMonth));

      const spending = transactionsData?.reduce(
        (sum, t) => sum + Number(t.amount),
        0
      ) || 0;

      setBudget(budgetData);
      setBudgetAmount(budgetData?.amount.toString() || '');
      setCurrentSpending(spending);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  }

  function getNextMonth(month: string) {
    const date = new Date(month + '-01');
    date.setMonth(date.getMonth() + 1);
    return date.toISOString().slice(0, 7);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    try {
      const amount = Number(budgetAmount);
      if (amount <= 0) return;

      if (budget) {
        await supabase
          .from('budgets')
          .update({ amount })
          .eq('id', budget.id);
      } else {
        await supabase.from('budgets').insert([
          {
            user_id: user?.id,
            month: currentMonth,
            amount,
          },
        ]);
      }

      loadData();
    } catch (error) {
      console.error('Error saving budget:', error);
    }
  }

  const budgetValue = budget?.amount || 0;
  const remaining = budgetValue - currentSpending;
  const percentage = budgetValue > 0 ? (currentSpending / budgetValue) * 100 : 0;
  const isExceeded = currentSpending > budgetValue;
  const isWarning = percentage > 70 && !isExceeded;

  if (loading) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-64">
          <LoadingSpinner size="lg" />
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          {t('budget.title')}
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="p-6">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  {t('budget.monthlyBudget')}
                </p>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white">
                  ₹{budgetValue.toLocaleString()}
                </h3>
              </div>
              <div className="p-3 rounded-xl bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400">
                <Wallet size={24} />
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  {t('budget.currentSpending')}
                </p>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white">
                  ₹{currentSpending.toLocaleString()}
                </h3>
              </div>
              <div className="p-3 rounded-xl bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400">
                <TrendingDown size={24} />
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  {t('budget.remaining')}
                </p>
                <h3
                  className={`text-2xl font-bold ${
                    isExceeded
                      ? 'text-red-600 dark:text-red-400'
                      : 'text-green-600 dark:text-green-400'
                  }`}
                >
                  ₹{remaining.toLocaleString()}
                </h3>
              </div>
              <div
                className={`p-3 rounded-xl ${
                  isExceeded
                    ? 'bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400'
                    : 'bg-green-50 dark:bg-green-900/20 text-green-600 dark:text-green-400'
                }`}
              >
                <AlertTriangle size={24} />
              </div>
            </div>
          </Card>
        </div>

        {isExceeded && (
          <div className="bg-red-50 dark:bg-red-900/20 border-l-4 border-red-500 p-4 rounded-lg">
            <div className="flex items-center gap-3">
              <AlertTriangle className="text-red-600 dark:text-red-400" size={24} />
              <div>
                <h3 className="font-semibold text-red-900 dark:text-red-100">
                  {t('budget.exceeded')}
                </h3>
                <p className="text-sm text-red-700 dark:text-red-300 mt-1">
                  You have exceeded your monthly budget by ₹
                  {Math.abs(remaining).toLocaleString()}. Consider reviewing your expenses.
                </p>
              </div>
            </div>
          </div>
        )}

        {isWarning && (
          <div className="bg-yellow-50 dark:bg-yellow-900/20 border-l-4 border-yellow-500 p-4 rounded-lg">
            <div className="flex items-center gap-3">
              <AlertTriangle className="text-yellow-600 dark:text-yellow-400" size={24} />
              <div>
                <h3 className="font-semibold text-yellow-900 dark:text-yellow-100">
                  {t('budget.warning')}
                </h3>
                <p className="text-sm text-yellow-700 dark:text-yellow-300 mt-1">
                  You have used {percentage.toFixed(0)}% of your monthly budget. Be mindful
                  of your spending.
                </p>
              </div>
            </div>
          </div>
        )}

        <Card className="p-6">
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-6">
            {t('budget.setBudget')}
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Budget Amount for {new Date(currentMonth).toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
              </label>
              <input
                type="number"
                required
                min="0"
                step="0.01"
                value={budgetAmount}
                onChange={(e) => setBudgetAmount(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500"
                placeholder="Enter budget amount"
              />
            </div>
            <button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 rounded-lg transition-colors"
            >
              {budget ? t('common.save') : t('budget.setBudget')}
            </button>
          </form>
        </Card>

        {budgetValue > 0 && (
          <Card className="p-6">
            <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-6">
              Budget Overview
            </h2>
            <div className="space-y-4">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600 dark:text-gray-400">
                  ₹{currentSpending.toLocaleString()} / ₹{budgetValue.toLocaleString()}
                </span>
                <span
                  className={`font-semibold ${
                    isExceeded
                      ? 'text-red-600 dark:text-red-400'
                      : percentage > 70
                      ? 'text-yellow-600 dark:text-yellow-400'
                      : 'text-green-600 dark:text-green-400'
                  }`}
                >
                  {Math.min(percentage, 100).toFixed(0)}%
                </span>
              </div>
              <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                <div
                  className={`h-full transition-all duration-500 ${
                    isExceeded
                      ? 'bg-red-500'
                      : percentage > 70
                      ? 'bg-yellow-500'
                      : 'bg-green-500'
                  }`}
                  style={{ width: `${Math.min(percentage, 100)}%` }}
                />
              </div>
              <div className="grid grid-cols-2 gap-4 pt-4">
                <div className="text-center p-4 bg-green-50 dark:bg-green-900/20 rounded-lg">
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    Budget Used
                  </p>
                  <p className="text-lg font-bold text-green-600 dark:text-green-400">
                    ₹{currentSpending.toLocaleString()}
                  </p>
                </div>
                <div className="text-center p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    Budget Left
                  </p>
                  <p
                    className={`text-lg font-bold ${
                      isExceeded
                        ? 'text-red-600 dark:text-red-400'
                        : 'text-blue-600 dark:text-blue-400'
                    }`}
                  >
                    ₹{Math.max(remaining, 0).toLocaleString()}
                  </p>
                </div>
              </div>
            </div>
          </Card>
        )}
      </div>
    </Layout>
  );
}
