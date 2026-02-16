import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import { Bell, Check, CheckCheck } from 'lucide-react';
import axios from 'axios';

interface Notification {
  id: number;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

export function Notifications() {
  const { t } = useTranslation();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    loadNotifications();
    loadUnreadCount();
  }, []);

  async function loadNotifications() {
    try {
      const response = await axios.get('http://localhost:8080/api/notifications');
      setNotifications(response.data.data);
    } catch (error) {
      console.error('Error loading notifications:', error);
    }
  }

  async function loadUnreadCount() {
    try {
      const response = await axios.get('http://localhost:8080/api/notifications/unread/count');
      setUnreadCount(response.data.data);
    } catch (error) {
      console.error('Error loading count:', error);
    }
  }

  async function markAsRead(id: number) {
    try {
      await axios.put(`http://localhost:8080/api/notifications/${id}/read`);
      loadNotifications();
      loadUnreadCount();
    } catch (error) {
      console.error('Error marking as read:', error);
    }
  }

  async function markAllAsRead() {
    try {
      await axios.put('http://localhost:8080/api/notifications/read-all');
      loadNotifications();
      loadUnreadCount();
    } catch (error) {
      console.error('Error marking all as read:', error);
    }
  }

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'ALERT': return 'bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400';
      case 'WARNING': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400';
      case 'INFO': return 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400';
      default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white">{t('notifications.title')}</h1>
            {unreadCount > 0 && (
              <span className="bg-red-500 text-white text-sm font-bold px-3 py-1 rounded-full">
                {unreadCount}
              </span>
            )}
          </div>
          {unreadCount > 0 && (
            <button
              onClick={markAllAsRead}
              className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors"
            >
              <CheckCheck size={20} />
              {t('notifications.markAllRead')}
            </button>
          )}
        </div>

        {notifications.length === 0 ? (
          <Card className="p-12 text-center">
            <Bell size={48} className="mx-auto text-gray-400 mb-4" />
            <p className="text-gray-600 dark:text-gray-400">{t('notifications.noNotifications')}</p>
          </Card>
        ) : (
          <div className="space-y-3">
            {notifications.map((notification) => (
              <Card
                key={notification.id}
                className={`p-4 ${!notification.isRead ? 'border-l-4 border-blue-500' : ''}`}
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <span className={`text-xs font-semibold px-2 py-1 rounded ${getTypeColor(notification.type)}`}>
                        {notification.type}
                      </span>
                      <span className="text-xs text-gray-500 dark:text-gray-400">
                        {new Date(notification.createdAt).toLocaleString()}
                      </span>
                    </div>
                    <p className="text-gray-900 dark:text-white">{notification.message}</p>
                  </div>
                  {!notification.isRead && (
                    <button
                      onClick={() => markAsRead(notification.id)}
                      className="text-blue-600 hover:text-blue-700 dark:text-blue-400"
                    >
                      <Check size={20} />
                    </button>
                  )}
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
}
