import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../services/auth';

function Login({ onLogin }) {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setInfo('');
    setLoading(true);

    try {
      const response = await login(formData);
      
      // Store Auth Session
      localStorage.setItem('token', response.token);
      const userPayload = {
        userId: response.userId,
        name: response.name,
        email: response.email,
        role: response.role
      };
      localStorage.setItem('user', JSON.stringify(userPayload));
      
      // Update App State
      onLogin(userPayload);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Oops! Those credentials do not match our records.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] flex items-center justify-center bg-stone-50/50 p-4">
      <div className="max-w-5xl w-full grid grid-cols-1 lg:grid-cols-2 bg-white rounded-3xl overflow-hidden shadow-2xl shadow-brand-900/10 border border-stone-100">
        
        {/* Left: Emotional Hero Section */}
        <div className="hidden lg:flex flex-col justify-center p-12 bg-brand-500 text-white relative overflow-hidden">
          {/* Decorative Circle Elements */}
          <div className="absolute -top-20 -left-20 w-64 h-64 bg-brand-400 rounded-full opacity-50 blur-3xl"></div>
          <div className="absolute -bottom-20 -right-20 w-64 h-64 bg-brand-600 rounded-full opacity-50 blur-3xl"></div>
          
          <div className="relative z-10">
            <span className="inline-block px-4 py-1 rounded-full bg-brand-400/30 text-brand-100 text-xs font-bold uppercase tracking-widest mb-6">
              Guardian Portal
            </span>
            <h2 className="text-5xl font-heading font-bold mb-6 leading-tight">
              Welcome back <br /> to AdoptMe
            </h2>
            <p className="text-brand-100 text-lg leading-relaxed mb-8 max-w-md">
              Your adoption journey continues here. Sign in to check your messages, manage listings, and find forever homes for pets in need.
            </p>
            <div className="flex items-center gap-4">
              <div className="flex -space-x-3">
                {[1, 2, 3].map(i => (
                  <div key={i} className="w-10 h-10 rounded-full border-2 border-brand-500 bg-slate-200" />
                ))}
              </div>
              <p className="text-sm font-medium text-brand-200">Joined by 2,000+ guardians</p>
            </div>
          </div>
        </div>

        {/* Right: Login Form Section */}
        <div className="p-8 md:p-16 flex flex-col justify-center">
          <div className="mb-10 text-center lg:text-left">
            <h3 className="text-3xl font-heading font-bold text-slate-900 mb-2">Sign In</h3>
            <p className="text-slate-500">Enter your account details below.</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-slate-400 ml-1">Email Address</label>
              <input
                type="email"
                name="email"
                placeholder="name@example.com"
                className="input-field"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2">
              <div className="flex justify-between items-center px-1">
                <label className="text-xs font-bold uppercase tracking-widest text-slate-400">Password</label>
                <button
                  type="button"
                  onClick={() => setInfo('We are working on this module. Please check back soon.')}
                  className="text-[10px] font-bold text-brand-500 hover:underline"
                >
                  Forgot?
                </button>
              </div>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  placeholder="********"
                  className="input-field pr-12"
                  value={formData.password}
                  onChange={handleChange}
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((prev) => !prev)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-xs font-semibold text-stone-500 hover:text-brand-600"
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
            </div>

            {error && (
              <div className="p-4 bg-red-50 rounded-xl border border-red-100 text-red-600 text-sm font-medium animate-shake">
                {error}
              </div>
            )}
            {info && (
              <div className="p-4 bg-brand-50 rounded-xl border border-brand-100 text-brand-700 text-sm font-medium">
                {info}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full btn-primary py-4 text-lg shadow-xl shadow-brand-500/20 active:scale-[0.98] transition-all"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Verifying...
                </span>
              ) : (
                'Access Dashboard'
              )}
            </button>
          </form>

          <p className="mt-10 text-center text-sm text-slate-500">
            Don't have an account yet?{' '}
            <Link to="/signup" className="text-brand-600 font-bold hover:underline">
              Join the community
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;
