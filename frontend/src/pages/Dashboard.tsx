import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { supabase } from '../services/supabase';
import { Layout } from '../components/Layout';
import { StatCard } from '../components/Card';
import { Loading } from '../components/Loading';
import { TrendingUp, TrendingDown, Wallet } from 'lucide-react';
import {
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';

interface Transaction {
  id: string;
  type: string;
  amount: number;
  category: string;
  date: string;
}

interface Budget {
  amount: number;
}

const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899'];

export function Dashboard() {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [budget, setBudget] = useState<Budget | null>(null);

  useEffect(() => {
    if (user) {
      loadData();
    }
  }, [user]);

  async function loadData() {
    try {
      const { data: transactionsData } = await supabase
        .from('transactions')
        .select('*')
        .eq('user_id', user?.id)
        .order('date', { ascending: false });

      const currentMonth = new Date().toISOString().slice(0, 7);
      const { data: budgetData } = await supabase
        .from('budgets')
        .select('*')
        .eq('user_id', user?.id)
        .eq('month', currentMonth)
        .maybeSingle();

      setTransactions(transactionsData || []);
      setBudget(budgetData);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  }

  const totalIncome = transactions
    .filter((t) => t.type === 'income')
    .reduce((sum, t) => sum + Number(t.amount), 0);

  const totalExpense = transactions
    .filter((t) => t.type === 'expense')
    .reduce((sum, t) => sum + Number(t.amount), 0);

  const remainingBalance = totalIncome - totalExpense;

  const categoryData = transactions
    .filter((t) => t.type === 'expense')
    .reduce((acc: { [key: string]: number }, t) => {
      acc[t.category] = (acc[t.category] || 0) + Number(t.amount);
      return acc;
    }, {});

  const pieData = Object.entries(categoryData).map(([name, value]) => ({
    name: t(`categories.${name}`) || name,
    value,
  }));

  const monthlyData = transactions.reduce(
    (acc: { [key: string]: { income: number; expense: number } }, t) => {
      const month = t.date.slice(0, 7);
      if (!acc[month]) {
        acc[month] = { income: 0, expense: 0 };
      }
      if (t.type === 'income') {
        acc[month].income += Number(t.amount);
      } else {
        acc[month].expense += Number(t.amount);
      }
      return acc;
    },
    {}
  );

  const barData = Object.entries(monthlyData)
    .map(([month, data]) => ({
      month,
      income: data.income,
      expense: data.expense,
    }))
    .sort((a, b) => a.month.localeCompare(b.month))
    .slice(-6);

  const budgetPercentage = budget
    ? Math.min((totalExpense / Number(budget.amount)) * 100, 100)
    : 0;

  if (loading) return <Loading />;

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          {t('dashboard.title')}
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <StatCard
            title={t('dashboard.totalIncome')}
            value={`₹${totalIncome.toLocaleString()}`}
            icon={<TrendingUp size={24} />}
            color="green"
          />
          <StatCard
            title={t('dashboard.totalExpense')}
            value={`₹${totalExpense.toLocaleString()}`}
            icon={<TrendingDown size={24} />}
            color="red"
          />
          <StatCard
            title={t('dashboard.remainingBalance')}
            value={`₹${remainingBalance.toLocaleString()}`}
            icon={<Wallet size={24} />}
            color="blue"
          />
        </div>

        {budget && (
          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              {t('dashboard.budgetProgress')}
            </h2>
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600 dark:text-gray-400">
                  ₹{totalExpense.toLocaleString()} / ₹
                  {Number(budget.amount).toLocaleString()}
                </span>
                <span
                  className={`font-semibold ${
                    budgetPercentage > 90
                      ? 'text-red-600'
                      : budgetPercentage > 70
                      ? 'text-yellow-600'
                      : 'text-green-600'
                  }`}
                >
                  {budgetPercentage.toFixed(0)}%
                </span>
              </div>
              <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                <div
                  className={`h-full transition-all duration-500 ${
                    budgetPercentage > 90
                      ? 'bg-red-500'
                      : budgetPercentage > 70
                      ? 'bg-yellow-500'
                      : 'bg-green-500'
                  }`}
                  style={{ width: `${budgetPercentage}%` }}
                />
              </div>
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-6">
              {t('dashboard.categoryWise')}
            </h2>
            {pieData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={pieData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={(entry) => `${entry.name}: ₹${entry.value}`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {pieData.map((_entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={COLORS[index % COLORS.length]}
                      />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <p className="text-center text-gray-500 dark:text-gray-400 py-12">
                {t('common.noData')}
              </p>
            )}
          </div>

          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-6">
              {t('dashboard.monthlyTrend')}
            </h2>
            {barData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={barData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="income" fill="#10B981" name="Income" />
                  <Bar dataKey="expense" fill="#EF4444" name="Expense" />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <p className="text-center text-gray-500 dark:text-gray-400 py-12">
                {t('common.noData')}
              </p>
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
}
