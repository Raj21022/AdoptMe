import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signup } from '../services/auth';

function Signup() {
  const navigate = useNavigate();
  const roleOptions = [
    {
      value: 'USER',
      title: 'Adopt a Pet',
      icon: 'Adopt',
      description: 'Find your new best friend.'
    },
    {
      value: 'COMMON_LISTER',
      title: 'Individual Rescuer',
      icon: 'Rescue',
      description: 'Help a pet find a home.'
    },
    {
      value: 'NGO_LISTER',
      title: 'NGO / Organization',
      icon: 'NGO',
      description: 'Manage shelter listings.'
    }
  ];

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'USER'
  });
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      if (formData.password !== confirmPassword) {
        setError('Passwords do not match');
        setLoading(false);
        return;
      }
      const message = await signup(formData);
      setSuccess(message || 'Account created! Sending OTP...');
      setTimeout(() => {
        navigate(`/verify-otp?email=${encodeURIComponent(formData.email)}`);
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.error || 'Signup failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] flex items-center justify-center bg-stone-50/50 p-4 py-12">
      <div className="max-w-6xl w-full grid grid-cols-1 lg:grid-cols-2 bg-white rounded-3xl overflow-hidden shadow-2xl shadow-brand-900/10 border border-stone-100">
        
        {/* Left: Branding & Context */}
        <div className="hidden lg:flex flex-col justify-center p-16 bg-brand-500 text-white relative overflow-hidden">
          <div className="absolute -top-20 -left-20 w-80 h-80 bg-brand-400 rounded-full opacity-40 blur-3xl" />
          
          <div className="relative z-10">
            <h2 className="text-5xl font-heading font-bold mb-8 leading-tight">
              Every pet deserves a <span className="text-brand-200 underline decoration-brand-300">loving home.</span>
            </h2>
            <div className="space-y-6">
              <div className="flex items-start gap-4">
                <div className="p-2 bg-brand-400/30 rounded-lg text-[10px] font-semibold uppercase tracking-wider">Verify</div>
                <div>
                  <h4 className="font-bold text-lg">Trusted Connections</h4>
                  <p className="text-brand-100 text-sm">We verify listers to ensure a safe environment for everyone.</p>
                </div>
              </div>
              <div className="flex items-start gap-4">
                <div className="p-2 bg-brand-400/30 rounded-lg text-[10px] font-semibold uppercase tracking-wider">Chat</div>
                <div>
                  <h4 className="font-bold text-lg">Direct Communication</h4>
                  <p className="text-brand-100 text-sm">Chat in real-time with adopters or rescuers seamlessly.</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Signup Form */}
        <div className="p-8 md:p-12">
          <div className="mb-8">
            <h3 className="text-3xl font-heading font-bold text-slate-900 mb-1">Create Account</h3>
            <p className="text-slate-500 text-sm">Join the community in a few simple steps.</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="space-y-1">
              <label className="text-[10px] font-bold uppercase tracking-widest text-slate-400 ml-1">Full Name</label>
              <input type="text" name="name" className="input-field" placeholder="Jane Doe" value={formData.name} onChange={handleChange} required />
            </div>
            <div className="space-y-1">
              <label className="text-[10px] font-bold uppercase tracking-widest text-slate-400 ml-1">Email</label>
              <input type="email" name="email" className="input-field" placeholder="jane@example.com" value={formData.email} onChange={handleChange} required />
            </div>

            <div className="space-y-1">
              <label className="text-[10px] font-bold uppercase tracking-widest text-slate-400 ml-1">Password</label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  className="input-field pr-12"
                  placeholder="********"
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

            <div className="space-y-1">
              <label className="text-[10px] font-bold uppercase tracking-widest text-slate-400 ml-1">Confirm Password</label>
              <div className="relative">
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  name="confirmPassword"
                  className="input-field pr-12"
                  placeholder="********"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword((prev) => !prev)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-xs font-semibold text-stone-500 hover:text-brand-600"
                >
                  {showConfirmPassword ? 'Hide' : 'Show'}
                </button>
              </div>
            </div>

            <div className="space-y-3">
              <label className="text-[10px] font-bold uppercase tracking-widest text-slate-400 ml-1">I am here to...</label>
              <div className="grid grid-cols-1 gap-2">
                {roleOptions.map((role) => (
                  <button
                    key={role.value}
                    type="button"
                    className={`flex items-center gap-4 p-3 rounded-2xl border-2 text-left transition-all ${
                      formData.role === role.value 
                        ? 'border-brand-500 bg-brand-50/50 ring-2 ring-brand-100' 
                        : 'border-slate-100 hover:border-slate-200'
                    }`}
                    onClick={() => setFormData({ ...formData, role: role.value })}
                  >
                    <div className="w-12 h-10 flex items-center justify-center bg-white rounded-xl shadow-sm text-[10px] font-semibold uppercase tracking-wider text-stone-600">
                      {role.icon}
                    </div>
                    <div>
                      <h4 className={`text-sm font-bold ${formData.role === role.value ? 'text-brand-600' : 'text-slate-700'}`}>{role.title}</h4>
                      <p className="text-[10px] text-slate-400 font-medium leading-tight">{role.description}</p>
                    </div>
                  </button>
                ))}
              </div>
            </div>

            {error && <div className="p-3 bg-red-50 text-red-600 rounded-xl text-xs font-bold border border-red-100">{error}</div>}
            {success && <div className="p-3 bg-emerald-50 text-emerald-600 rounded-xl text-xs font-bold border border-emerald-100">{success}</div>}

            <button type="submit" disabled={loading} className="w-full btn-primary py-4 text-lg shadow-xl shadow-brand-500/20 active:scale-[0.98]">
              {loading ? 'Processing...' : 'Get Started'}
            </button>
          </form>

          <p className="mt-8 text-center text-sm text-slate-500">
            Already have an account? <Link to="/login" className="text-brand-600 font-bold hover:underline">Sign In</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Signup;
