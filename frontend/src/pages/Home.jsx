import React, { useState, useEffect } from 'react';
import PetCard from '../components/PetCard';
import { getAllPets } from '../services/pet';

function Home({ user }) {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [locationQuery, setLocationQuery] = useState('');
  const [typeQuery, setTypeQuery] = useState('');

  useEffect(() => {
    loadPets('', '');
  }, []);

  const loadPets = async (location = '', type = '') => {
    setLoading(true);
    setError('');
    try {
      const data = await getAllPets({ location, type });
      setPets(data);
    } catch (err) {
      setError('Failed to load pets');
    } finally {
      setLoading(false);
    }
  };

  const handlePetDeleted = (deletedPetId) => {
    setPets((prevPets) => prevPets.filter((pet) => pet.id !== deletedPetId));
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    await loadPets(locationQuery, typeQuery);
  };

  const handleReset = async () => {
    setLocationQuery('');
    setTypeQuery('');
    await loadPets('', '');
  };

  return (
    <main className="page-enter min-h-screen pb-20">
      {/* 1. Hero Section */}
      <section className="relative pt-16 pb-20 overflow-hidden">
        <div className="container mx-auto px-4 relative z-10 text-center">
          <h1 className="text-4xl md:text-6xl font-heading mb-6 max-w-3xl mx-auto">
            Find Your New <span className="text-brand-500">Best Friend</span>
          </h1>
          <p className="text-lg text-slate-600 max-w-2xl mx-auto mb-10 leading-relaxed">
            Connecting compassionate hearts with pets in need. Browse trusted listings from verified rescuers and NGOs.
          </p>

          {/* 2. Integrated Search Bar */}
          <div className="max-w-4xl mx-auto">
            <form 
              onSubmit={handleSearch} 
              className="glass-card p-2 md:p-3 flex flex-col md:flex-row items-stretch gap-2 shadow-xl shadow-brand-500/10"
            >
              <div className="flex-1 relative">
                <input
                  type="text"
                  className="w-full px-4 py-4 bg-transparent outline-none text-slate-700 font-medium"
                  placeholder="City or State..."
                  value={locationQuery}
                  onChange={(e) => setLocationQuery(e.target.value)}
                />
              </div>
              
              <div className="hidden md:block w-px bg-slate-200 my-2" />

              <div className="flex-1 relative">
                <select
                  className="w-full px-4 py-4 bg-transparent outline-none text-slate-700 font-medium appearance-none cursor-pointer"
                  value={typeQuery}
                  onChange={(e) => setTypeQuery(e.target.value)}
                >
                  <option value="">All Animals</option>
                  <option value="Dog">Dogs</option>
                  <option value="Cat">Cats</option>
                  <option value="Bird">Birds</option>
                  <option value="Others">Others</option>
                </select>
              </div>

              <div className="flex gap-2">
                <button type="button" onClick={handleReset} className="px-6 py-4 text-slate-400 hover:text-slate-600 transition-colors font-semibold">
                  Reset
                </button>
                <button type="submit" className="btn-primary py-4 px-10">
                  Search
                </button>
              </div>
            </form>
          </div>
        </div>
      </section>

      {/* 3. Results Section */}
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-2xl font-heading">Available for Adoption</h2>
          <span className="bg-brand-50 text-brand-600 px-4 py-1 rounded-full text-sm font-bold border border-brand-100">
            {pets.length} {pets.length === 1 ? 'Friend' : 'Friends'} Found
          </span>
        </div>

        {loading ? (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="w-12 h-12 border-4 border-brand-200 border-t-brand-500 rounded-full animate-spin mb-4"></div>
            <p className="text-slate-500 font-medium">Looking for pets...</p>
          </div>
        ) : error ? (
          <div className="glass-card p-10 text-center border-red-100 bg-red-50/30">
            <p className="text-red-600 font-semibold mb-2">Oops! {error}</p>
            <button onClick={handleReset} className="text-brand-600 underline">Try again</button>
          </div>
        ) : pets.length === 0 ? (
          <div className="glass-card p-20 text-center">
            <div className="text-[10px] font-semibold uppercase tracking-wider text-stone-400 mb-6">No pets</div>
            <h3 className="text-xl font-bold mb-2">No pets found</h3>
            <p className="text-slate-500">We couldn't find any pets matching those criteria. Try widening your search!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
            {pets.map((pet) => (
              <PetCard
                key={pet.id}
                pet={pet}
                currentUser={user}
                onPetDeleted={handlePetDeleted}
              />
            ))}
          </div>
        )}
      </div>
    </main>
  );
}

export default Home;
