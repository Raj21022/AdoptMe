import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';

function Navbar({ isAuthenticated, user, onLogout }) {
  const navigate = useNavigate();
  const location = useLocation();
  const roleLabel = getRoleLabel(user?.role);

  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };

  // Helper to highlight active links
  const isActive = (path) => location.pathname === path;
  const linkClass = (path) => `
    px-4 py-2 rounded-lg transition-all font-medium text-sm
    ${isActive(path) 
      ? 'bg-brand-50 text-brand-600' 
      : 'text-slate-600 hover:text-brand-500 hover:bg-slate-50'}
  `;

  return (
    <nav className="sticky top-0 z-50 w-full bg-white/70 backdrop-blur-lg border-b border-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-18 py-4">
          
          {/* Logo Section */}
          <Link to="/" className="flex items-center gap-2 group">
            <span className="font-heading text-2xl font-bold text-stone-900">
              AdoptMe
            </span>
          </Link>

          {/* Navigation Links */}
          <div className="hidden md:flex items-center gap-2">
            {isAuthenticated ? (
              <>
                <Link to="/" className={linkClass('/')}>Browse Pets</Link>
                <Link to="/messages" className={linkClass('/messages')}>Messages</Link>
                
                {(user?.role === 'COMMON_LISTER' || user?.role === 'NGO_LISTER') && (
                  <Link 
                    to="/add-pet" 
                    className="ml-2 px-5 py-2 bg-warm-400 hover:bg-warm-500 text-white rounded-xl text-sm font-semibold shadow-sm transition-all active:scale-95"
                  >
                    + Post a Pet
                  </Link>
                )}

                <div className="h-6 w-[1px] bg-slate-200 mx-3" />

                <div className="flex flex-col items-end mr-3">
                  <span className="text-[11px] font-bold uppercase tracking-wider text-brand-500 leading-none mb-1">
                    {roleLabel}
                  </span>
                  <span className="text-sm font-semibold text-slate-800 leading-none">
                    {user?.name}
                  </span>
                </div>

                <button 
                  onClick={handleLogout} 
                  className="px-4 py-2 text-sm font-medium text-slate-500 hover:text-red-500 transition-colors"
                >
                  Logout
                </button>
              </>
            ) : (
              <div className="flex items-center gap-4">
                <Link to="/login" className="text-slate-600 font-medium hover:text-brand-500">
                  Login
                </Link>
                <Link to="/signup" className="btn-primary py-2 px-6">
                  Join Platform
                </Link>
              </div>
            )}
          </div>

          {/* Mobile Menu Icon (Placeholder for functionality) */}
          <div className="md:hidden">
            <button className="p-2 text-slate-600">
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16m-7 6h7" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}

function getRoleLabel(role) {
  if (role === 'NGO_LISTER') return 'Verified NGO';
  if (role === 'COMMON_LISTER') return 'Private Lister';
  return 'Pet Seeker';
}

export default Navbar;
