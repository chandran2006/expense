import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import { Upload, Camera, CheckCircle } from 'lucide-react';
import axios from 'axios';

export function ReceiptScanner() {
  const { t } = useTranslation();
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const selectedFile = e.target.files[0];
      setFile(selectedFile);
      setPreview(URL.createObjectURL(selectedFile));
      setResult(null);
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    setLoading(true);
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axios.post('http://localhost:8080/api/features/receipt/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setResult(response.data.data);
    } catch (error) {
      console.error('Error uploading receipt:', error);
      alert(t('receiptScanner.failed'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">{t('receiptScanner.title')}</h1>

        <Card className="p-6">
          <div className="text-center">
            <Camera size={64} className="mx-auto text-gray-400 mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
              {t('receiptScanner.uploadReceipt')}
            </h3>
            <p className="text-gray-600 dark:text-gray-400 mb-6">
              {t('receiptScanner.subtitle')}
            </p>

            <label className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg cursor-pointer transition-colors">
              <Upload size={20} />
              {t('receiptScanner.chooseFile')}
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />
            </label>
          </div>

          {preview && (
            <div className="mt-6">
              <img
                src={preview}
                alt="Receipt preview"
                className="max-w-md mx-auto rounded-lg border-2 border-gray-300 dark:border-gray-600"
              />
              <div className="text-center mt-4">
                <button
                  onClick={handleUpload}
                  disabled={loading}
                  className="bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-lg transition-colors disabled:opacity-50"
                >
                  {loading ? t('receiptScanner.processing') : t('receiptScanner.scanReceipt')}
                </button>
              </div>
            </div>
          )}
        </Card>

        {result && (
          <Card className="p-6">
            <div className="flex items-center gap-2 mb-4">
              <CheckCircle className="text-green-600" size={24} />
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
                {t('receiptScanner.success')}
              </h3>
            </div>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600 dark:text-gray-400">{t('common.amount')}:</span>
                <span className="font-semibold text-gray-900 dark:text-white">
                  ₹{result.amount}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600 dark:text-gray-400">{t('common.category')}:</span>
                <span className="font-semibold text-gray-900 dark:text-white">
                  {result.category}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600 dark:text-gray-400">{t('common.date')}:</span>
                <span className="font-semibold text-gray-900 dark:text-white">
                  {result.date}
                </span>
              </div>
              {result.merchantName && (
                <div className="flex justify-between">
                  <span className="text-gray-600 dark:text-gray-400">{t('receiptScanner.merchant')}:</span>
                  <span className="font-semibold text-gray-900 dark:text-white">
                    {result.merchantName}
                  </span>
                </div>
              )}
            </div>
          </Card>
        )}
      </div>
    </Layout>
  );
}
