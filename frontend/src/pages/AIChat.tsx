import { useState, useRef, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Layout } from '../components/Layout';
import { LoadingSpinner } from '../components/Loading';
import { Send, Bot, User as UserIcon } from 'lucide-react';
import axios from 'axios';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

export function AIChat() {
  const { t } = useTranslation();
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'assistant',
      content: '👋 Welcome to FinMate AI! I\'m your personal financial assistant.\n\nI can help you with:\n• Financial health analysis\n• Spending insights\n• Budget tracking\n• Saving tips\n• Expense comparisons\n\nTry the quick actions below or ask me anything!',
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const quickActions = [
    { label: '💯 Health Score', query: 'What\'s my financial health score?' },
    { label: '💰 Total Spending', query: 'How much did I spend this month?' },
    { label: '📊 Top Categories', query: 'Show my top spending categories' },
    { label: '💡 Saving Tips', query: 'Give me saving tips' },
    { label: '📈 Compare Months', query: 'Compare with last month' },
    { label: '💵 Balance', query: 'What\'s my current balance?' },
  ];

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!input.trim() || loading) return;

    sendMessage(input);
  }

  async function sendMessage(messageText: string) {
    if (!messageText.trim() || loading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: messageText,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const response = await axios.post('http://localhost:8080/api/ai/chat', {
        message: messageText,
      });

      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: response.data.data?.response || response.data.response || 'Response received',
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, assistantMessage]);
    } catch (error: any) {
      let errorMsg = 'Sorry, I encountered an error.';
      
      if (error.code === 'ERR_NETWORK' || error.message?.includes('Network Error')) {
        errorMsg = '🔴 Backend server is not running!\n\nPlease start the backend:\n1. Open terminal\n2. cd backend\n3. Run: start.bat\n\nOr double-click: start-all.bat';
      } else if (error.response?.status === 401 || error.response?.status === 403) {
        errorMsg = '🔒 Authentication required. Please login again.';
      } else if (error.response?.data?.message) {
        errorMsg = error.response.data.message;
      }
      
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: errorMsg,
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
      console.error('Error sending message:', error);
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout>
      <div className="h-[calc(100vh-8rem)] flex flex-col">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">
          {t('aiChat.title')}
        </h1>

        <div className="flex-1 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 flex flex-col overflow-hidden">
          <div className="flex-1 overflow-y-auto p-6 space-y-4">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`flex gap-3 ${
                  message.role === 'user' ? 'flex-row-reverse' : 'flex-row'
                }`}
              >
                <div
                  className={`flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center ${
                    message.role === 'user'
                      ? 'bg-blue-600 text-white'
                      : 'bg-green-100 dark:bg-green-900/20 text-green-600 dark:text-green-400'
                  }`}
                >
                  {message.role === 'user' ? (
                    <UserIcon size={20} />
                  ) : (
                    <Bot size={20} />
                  )}
                </div>
                <div
                  className={`flex-1 max-w-[80%] ${
                    message.role === 'user' ? 'flex flex-col items-end' : ''
                  }`}
                >
                  <div
                    className={`rounded-2xl px-4 py-3 ${
                      message.role === 'user'
                        ? 'bg-blue-600 text-white'
                        : 'bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white'
                    }`}
                  >
                    <p className="text-sm whitespace-pre-wrap">{message.content}</p>
                  </div>
                  <span className="text-xs text-gray-500 dark:text-gray-400 mt-1 px-2">
                    {message.timestamp.toLocaleTimeString()}
                  </span>
                </div>
              </div>
            ))}
            {loading && (
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-10 h-10 rounded-full bg-green-100 dark:bg-green-900/20 text-green-600 dark:text-green-400 flex items-center justify-center">
                  <Bot size={20} />
                </div>
                <div className="flex-1">
                  <div className="bg-gray-100 dark:bg-gray-700 rounded-2xl px-4 py-3 inline-flex items-center gap-2">
                    <LoadingSpinner size="sm" />
                    <span className="text-sm text-gray-600 dark:text-gray-300">
                      {t('aiChat.typing')}
                    </span>
                  </div>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          <div className="border-t border-gray-200 dark:border-gray-700 p-4">
            <div className="mb-3 flex flex-wrap gap-2">
              {quickActions.map((action, index) => (
                <button
                  key={index}
                  onClick={() => sendMessage(action.query)}
                  disabled={loading}
                  className="text-xs px-3 py-1.5 rounded-full bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 hover:bg-blue-100 dark:hover:bg-blue-900/30 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {action.label}
                </button>
              ))}
            </div>
            <form onSubmit={handleSubmit} className="flex gap-3">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                disabled={loading}
                placeholder={t('aiChat.placeholder')}
                className="flex-1 px-4 py-3 rounded-xl border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all disabled:opacity-50"
              />
              <button
                type="submit"
                disabled={loading || !input.trim()}
                className="bg-blue-600 hover:bg-blue-700 text-white p-3 rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
              >
                <Send size={20} />
              </button>
            </form>
          </div>
        </div>
      </div>
    </Layout>
  );
}
