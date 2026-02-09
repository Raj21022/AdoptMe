import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { resendOtp, verifyOtp } from '../services/auth';

function VerifyOtp() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [formData, setFormData] = useState({
    email: '',
    otp: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);

  useEffect(() => {
    const emailFromQuery = searchParams.get('email');
    if (emailFromQuery) {
      setFormData((prev) => ({ ...prev, email: emailFromQuery }));
    }
  }, [searchParams]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleVerify = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const message = await verifyOtp(formData.email, formData.otp);
      setSuccess(message || 'Account verified successfully!');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.error || 'OTP verification failed');
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (!formData.email) {
      setError('Please enter your email first');
      return;
    }
    setError('');
    setSuccess('');
    setResending(true);
    try {
      const message = await resendOtp(formData.email);
      setSuccess(message || 'A new code has been sent!');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to resend OTP');
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] flex items-center justify-center bg-stone-50/50 p-4">
      <div className="max-w-4xl w-full grid grid-cols-1 lg:grid-cols-2 bg-white rounded-3xl overflow-hidden shadow-2xl shadow-brand-900/10 border border-stone-100">
        
        {/* Left Side: Illustration & Instructions */}
        <div className="hidden lg:flex flex-col justify-center p-12 bg-brand-700 text-white relative">
          <div className="relative z-10">
            <div className="w-16 h-16 bg-brand-500 rounded-2xl flex items-center justify-center text-3xl mb-8 shadow-lg shadow-brand-500/20">
              OTP
            </div>
            <h2 className="text-4xl font-heading font-bold mb-4">Check your inbox.</h2>
            <p className="text-slate-400 text-lg leading-relaxed">
              We've sent a 6-digit verification code to <span className="text-brand-400 font-bold">{formData.email || 'your email'}</span>.
            </p>
            <div className="mt-12 p-6 bg-slate-800/50 rounded-2xl border border-slate-700">
              <p className="text-sm text-slate-300 italic">
                "Security first. This helps us ensure every pet listed is managed by a real, reachable human."
              </p>
            </div>
          </div>
        </div>

        {/* Right Side: Form */}
        <div className="p-8 md:p-16 flex flex-col justify-center">
          <div className="mb-10 text-center lg:text-left">
            <h3 className="text-3xl font-heading font-bold text-slate-900 mb-2">Verify OTP</h3>
            <p className="text-slate-500">Enter the code to activate your account.</p>
          </div>

          <form onSubmit={handleVerify} className="space-y-6">
            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Email Address</label>
              <input
                type="email"
                name="email"
                className="input-field bg-slate-50"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">6-Digit Code</label>
              <input
                type="text"
                name="otp"
                value={formData.otp}
                onChange={handleChange}
                maxLength="6"
                inputMode="numeric"
                pattern="[0-9]{6}"
                placeholder="0 0 0 0 0 0"
                className="input-field text-center text-2xl font-bold tracking-[0.5em] placeholder:text-slate-200"
                required
              />
            </div>

            {error && <div className="p-4 bg-red-50 text-red-600 rounded-xl text-sm font-medium border border-red-100">{error}</div>}
            {success && <div className="p-4 bg-emerald-50 text-emerald-600 rounded-xl text-sm font-medium border border-emerald-100">{success}</div>}

            <div className="space-y-3">
              <button
                type="submit"
                disabled={loading}
                className="w-full btn-primary py-4 text-lg shadow-xl shadow-brand-500/20 active:scale-[0.98]"
              >
                {loading ? 'Validating...' : 'Confirm & Activate'}
              </button>
              
              <button
                type="button"
                onClick={handleResend}
                disabled={resending}
                className="w-full py-3 text-sm font-bold text-slate-400 hover:text-brand-600 transition-colors"
              >
                {resending ? 'Sending new code...' : 'Did not get a code? Resend'}
              </button>
            </div>
          </form>

          <p className="mt-8 text-center text-sm">
            <Link to="/login" className="text-slate-400 hover:text-slate-600 underline">Back to Login</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default VerifyOtp;
