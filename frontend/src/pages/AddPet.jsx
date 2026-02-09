import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addPet, uploadImage } from '../services/pet';

function AddPet() {
  const navigate = useNavigate();
  const animalTypes = ['Dog', 'Cat', 'Bird', 'Reptile', 'Aquatic'];
  const [formData, setFormData] = useState({
    name: '',
    age: '',
    type: 'Dog',
    description: '',
    contactNumber: '',
    location: '',
    landmark: '',
    locationLink: '',
    vaccinationStatus: 'Unknown',
    stray: false,
    adoptionStatus: 'AVAILABLE',
    imageUrls: []
  });
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileSelect = (e) => {
    setSelectedFiles(Array.from(e.target.files));
  };

  const uploadFiles = async (files) => {
    const uploadPromises = files.map((file) => uploadImage(file));
    return Promise.all(uploadPromises);
  };

  const handleUploadImages = async () => {
    if (selectedFiles.length === 0) return;
    setUploading(true);
    setError('');
    try {
      const imageUrls = await uploadFiles(selectedFiles);
      setFormData((prev) => ({
        ...prev,
        imageUrls: [...prev.imageUrls, ...imageUrls]
      }));
      setSelectedFiles([]);
      setSuccess('Images uploaded!');
    } catch (err) {
      setError('Failed to upload images');
    } finally {
      setUploading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      let imageUrls = [...formData.imageUrls];
      if (selectedFiles.length > 0) {
        setUploading(true);
        const uploadedUrls = await uploadFiles(selectedFiles);
        imageUrls = [...imageUrls, ...uploadedUrls];
      }

      const petData = {
        ...formData,
        imageUrls,
        age: formData.age ? parseInt(formData.age, 10) : null
      };
      await addPet(petData);
      setSuccess('Listing published successfully!');
      setTimeout(() => navigate('/'), 1500);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to add pet');
    } finally {
      setLoading(false);
      setUploading(false);
    }
  };

  const removeUploadedImage = (indexToRemove) => {
    setFormData((prev) => ({
      ...prev,
      imageUrls: prev.imageUrls.filter((_, index) => index !== indexToRemove)
    }));
  };

  return (
    <div className="page-enter max-w-3xl mx-auto px-4 py-12">
      <div className="glass-card p-8 md:p-12 shadow-2xl shadow-brand-500/5">
        <header className="mb-10 text-center">
          <div className="inline-block p-3 bg-brand-50 rounded-2xl mb-4">
            <span className="text-[10px] font-semibold text-brand-700 uppercase tracking-wider">Listing</span>
          </div>
          <h1 className="text-3xl font-heading mb-2">Create a Pet Listing</h1>
          <p className="text-slate-500">Share Buddy's story and find them a forever home.</p>
        </header>

        <form onSubmit={handleSubmit} className="space-y-8">
          {/* Section: Basic Info */}
          <div className="space-y-4">
            <h3 className="text-sm font-bold uppercase tracking-widest text-brand-500">Basic Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Pet Name *</label>
                <input
                  type="text" name="name" required
                  className="input-field" placeholder="e.g. Luna"
                  value={formData.name} onChange={handleChange}
                />
              </div>
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Animal Type *</label>
                <select
                  name="type" required className="input-field appearance-none cursor-pointer"
                  value={formData.type} onChange={handleChange}
                >
                  {animalTypes.map((type) => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>
            </div>
            <div className="space-y-1">
              <label className="text-sm font-semibold ml-1">Approximate Age (Years)</label>
              <input
                type="number" name="age" min="0"
                className="input-field" placeholder="e.g. 2"
                value={formData.age} onChange={handleChange}
              />
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Adoption Status</label>
                <select
                  name="adoptionStatus"
                  className="input-field"
                  value={formData.adoptionStatus}
                  onChange={handleChange}
                >
                  <option value="AVAILABLE">Available</option>
                  <option value="PENDING">Pending</option>
                  <option value="ADOPTED">Adopted</option>
                </select>
              </div>
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Stray Animal</label>
                <select
                  name="stray"
                  className="input-field"
                  value={formData.stray ? 'true' : 'false'}
                  onChange={(e) => setFormData((prev) => ({ ...prev, stray: e.target.value === 'true' }))}
                >
                  <option value="false">No, owned or fostered</option>
                  <option value="true">Yes, stray</option>
                </select>
              </div>
            </div>
          </div>

          {/* Section: Photos */}
          <div className="space-y-4">
            <h3 className="text-sm font-bold uppercase tracking-widest text-brand-500">Photos</h3>
            <div className="border-2 border-dashed border-slate-200 rounded-2xl p-8 text-center bg-slate-50/50 hover:bg-slate-50 transition-colors">
              <input
                type="file" accept="image/*" multiple
                id="file-upload" className="hidden"
                onChange={handleFileSelect}
              />
              <label htmlFor="file-upload" className="cursor-pointer group">
                <div className="text-[10px] font-semibold text-stone-500 mb-2 group-hover:scale-110 transition-transform uppercase tracking-wider">Upload</div>
                <p className="text-sm font-semibold text-slate-700">Click to upload photos</p>
                <p className="text-xs text-slate-400 mt-1">PNG, JPG up to 10MB</p>
              </label>
            </div>

            {selectedFiles.length > 0 && (
              <div className="flex items-center justify-between p-4 bg-warm-50 border border-warm-100 rounded-xl">
                <span className="text-sm font-medium text-warm-700">{selectedFiles.length} files ready</span>
                <button
                  type="button" onClick={handleUploadImages} disabled={uploading}
                  className="text-sm font-bold text-warm-600 hover:text-warm-700 underline"
                >
                  {uploading ? 'Uploading...' : 'Confirm Upload'}
                </button>
              </div>
            )}

            {formData.imageUrls.length > 0 && (
              <div className="grid grid-cols-3 md:grid-cols-4 gap-3 mt-4">
                {formData.imageUrls.map((url, index) => (
                  <div key={index} className="relative group aspect-square rounded-xl overflow-hidden border border-slate-200">
                    <img src={url} alt="Pet" className="w-full h-full object-cover" />
                    <button
                      type="button" onClick={() => removeUploadedImage(index)}
                      className="absolute top-1 right-1 bg-white/90 backdrop-blur rounded-full w-6 h-6 flex items-center justify-center text-red-500 shadow-sm opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      X
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Section: Details & Contact */}
          <div className="space-y-4">
            <h3 className="text-sm font-bold uppercase tracking-widest text-brand-500">About & Contact</h3>
            <div className="space-y-1">
              <label className="text-sm font-semibold ml-1">Description</label>
              <textarea
                name="description" rows="4"
                className="input-field resize-none"
                placeholder="Talk about their personality, temperament, and medical history..."
                value={formData.description} onChange={handleChange}
              />
            </div>
            <div className="space-y-1">
              <label className="text-sm font-semibold ml-1">Vaccination Status</label>
              <select
                name="vaccinationStatus"
                className="input-field"
                value={formData.vaccinationStatus}
                onChange={handleChange}
              >
                <option value="Unknown">Unknown</option>
                <option value="Not vaccinated">Not vaccinated</option>
                <option value="Partially vaccinated">Partially vaccinated</option>
                <option value="Fully vaccinated">Fully vaccinated</option>
              </select>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Location *</label>
                <input
                  type="text" name="location" required
                  className="input-field" placeholder="City, State"
                  value={formData.location} onChange={handleChange}
                />
              </div>
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Contact Phone *</label>
                <input
                  type="tel" name="contactNumber" required
                  className="input-field" placeholder="+1..."
                  value={formData.contactNumber} onChange={handleChange}
                />
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Nearby Landmark</label>
                <input
                  type="text" name="landmark"
                  className="input-field" placeholder="e.g. Central Park Gate"
                  value={formData.landmark} onChange={handleChange}
                />
              </div>
              <div className="space-y-1">
                <label className="text-sm font-semibold ml-1">Maps Link</label>
                <input
                  type="url" name="locationLink"
                  className="input-field" placeholder="https://maps.google.com/..."
                  value={formData.locationLink} onChange={handleChange}
                />
              </div>
            </div>
          </div>

          {/* Status Messages */}
          {error && <div className="p-4 bg-red-50 text-red-600 rounded-xl text-sm font-medium border border-red-100">{error}</div>}
          {success && <div className="p-4 bg-emerald-50 text-emerald-600 rounded-xl text-sm font-medium border border-emerald-100">{success}</div>}

          <button
            type="submit"
            disabled={loading || uploading}
            className="w-full py-4 bg-warm-400 hover:bg-warm-500 text-white font-bold rounded-2xl shadow-xl shadow-warm-400/20 transition-all active:scale-[0.98] disabled:opacity-50"
          >
            {loading ? 'Publishing Listing...' : 'Publish Listing'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default AddPet;
