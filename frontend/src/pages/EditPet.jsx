import React, { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { getPetById, updatePet, uploadImage } from '../services/pet';

function EditPet({ user }) {
  const navigate = useNavigate();
  const { id } = useParams();
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
  const [loadingPet, setLoadingPet] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isOwner, setIsOwner] = useState(true);

  useEffect(() => {
    const loadPet = async () => {
      setLoadingPet(true);
      setError('');
      try {
        const pet = await getPetById(id);
        if (user?.userId !== pet.listedById) {
          setIsOwner(false);
          setError('Authorization Error: You can only edit your own listings.');
          return;
        }

        setFormData({
          name: pet.name || '',
          age: pet.age ?? '',
          type: pet.type || '',
          description: pet.description || '',
          contactNumber: pet.contactNumber || '',
          location: pet.location || '',
          landmark: pet.landmark || '',
          locationLink: pet.locationLink || '',
          vaccinationStatus: pet.vaccinationStatus || 'Unknown',
          stray: Boolean(pet.stray),
          adoptionStatus: pet.adoptionStatus || 'AVAILABLE',
          imageUrls: Array.isArray(pet.imageUrls) ? pet.imageUrls : []
        });
      } catch (err) {
        setError(err.response?.data?.error || 'Failed to load pet details');
      } finally {
        setLoadingPet(false);
      }
    };
    loadPet();
  }, [id, user?.userId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileSelect = (e) => setSelectedFiles(Array.from(e.target.files));

  const handleUploadImages = async () => {
    if (selectedFiles.length === 0) return;
    setUploading(true);
    setError('');
    try {
      const imageUrls = await Promise.all(selectedFiles.map((file) => uploadImage(file)));
      setFormData((prev) => ({
        ...prev,
        imageUrls: [...prev.imageUrls, ...imageUrls]
      }));
      setSelectedFiles([]);
      setSuccess('Gallery updated!');
    } catch (err) {
      setError('Failed to upload images');
    } finally {
      setUploading(false);
    }
  };

  const removeImage = (index) => {
    setFormData((prev) => ({
      ...prev,
      imageUrls: prev.imageUrls.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSaving(true);

    try {
      let imageUrls = [...formData.imageUrls];
      if (selectedFiles.length > 0) {
        setUploading(true);
        const uploadedUrls = await Promise.all(selectedFiles.map((file) => uploadImage(file)));
        imageUrls = [...imageUrls, ...uploadedUrls];
      }

      const payload = {
        ...formData,
        imageUrls,
        age: formData.age ? parseInt(formData.age, 10) : null
      };

      await updatePet(id, payload);
      setSuccess('Update successful!');
      setTimeout(() => navigate(`/pets/${id}`), 1500);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to update pet');
    } finally {
      setSaving(false);
      setUploading(false);
    }
  };

  if (loadingPet) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="text-slate-400 font-heading animate-pulse">Loading Listing...</div>
      </div>
    );
  }

  if (!isOwner) {
    return (
      <div className="page-enter max-w-xl mx-auto py-20 px-4">
        <div className="glass-card p-10 text-center border-red-100">
          <div className="text-[10px] font-semibold uppercase tracking-wider text-stone-400 mb-4">Access</div>
          <h2 className="text-xl font-bold text-slate-800 mb-2">Access Denied</h2>
          <p className="text-slate-500 mb-6">{error}</p>
          <Link to="/" className="btn-primary inline-block px-8">Back to Safety</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page-enter max-w-4xl mx-auto px-4 py-12">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-heading font-bold text-slate-900">Edit Listing</h1>
          <p className="text-slate-500">Managing <span className="text-brand-600 font-bold">{formData.name}</span></p>
        </div>
        <Link to={`/pets/${id}`} className="text-sm font-bold text-brand-500 hover:text-brand-600">
          View Public Page 
        </Link>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Form Section */}
        <form onSubmit={handleSubmit} className="lg:col-span-2 space-y-6">
          <div className="glass-card p-6 md:p-8 space-y-6 shadow-xl shadow-slate-200/50">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Pet Name</label>
                <input type="text" name="name" className="input-field" value={formData.name} onChange={handleChange} required />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Species</label>
                <select name="type" className="input-field" value={formData.type} onChange={handleChange} required>
                  {animalTypes.map(t => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
            </div>

            <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Description</label>
                <textarea name="description" rows="5" className="input-field resize-none" value={formData.description} onChange={handleChange} />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Location</label>
                <input type="text" name="location" className="input-field" value={formData.location} onChange={handleChange} required />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Contact Phone</label>
                <input type="tel" name="contactNumber" className="input-field" value={formData.contactNumber} onChange={handleChange} required />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Adoption Status</label>
                <select name="adoptionStatus" className="input-field" value={formData.adoptionStatus} onChange={handleChange}>
                  <option value="AVAILABLE">Available</option>
                  <option value="PENDING">Pending</option>
                  <option value="ADOPTED">Adopted</option>
                </select>
              </div>
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Stray Animal</label>
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

            <div className="space-y-1">
              <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Vaccination Status</label>
              <select name="vaccinationStatus" className="input-field" value={formData.vaccinationStatus} onChange={handleChange}>
                <option value="Unknown">Unknown</option>
                <option value="Not vaccinated">Not vaccinated</option>
                <option value="Partially vaccinated">Partially vaccinated</option>
                <option value="Fully vaccinated">Fully vaccinated</option>
              </select>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Nearby Landmark</label>
                <input type="text" name="landmark" className="input-field" value={formData.landmark} onChange={handleChange} />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Maps Link</label>
                <input type="url" name="locationLink" className="input-field" value={formData.locationLink} onChange={handleChange} />
              </div>
            </div>
          </div>

          <button 
            type="submit" 
            disabled={saving || uploading}
            className="w-full py-4 bg-emerald-500 hover:bg-emerald-600 text-white font-bold rounded-2xl shadow-lg shadow-emerald-500/20 transition-all active:scale-[0.98]"
          >
            {saving ? 'Saving Changes...' : 'Update Listing'}
          </button>
          
          {error && <p className="text-center text-red-500 text-sm font-medium">{error}</p>}
          {success && <p className="text-center text-emerald-500 text-sm font-medium">{success}</p>}
        </form>

        {/* Media Sidebar */}
        <div className="space-y-6">
          <div className="glass-card p-6">
            <h3 className="font-bold text-slate-800 mb-4">Gallery Management</h3>
            
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-2">
                {formData.imageUrls.map((url, index) => (
                  <div key={index} className="relative aspect-square rounded-lg overflow-hidden border border-slate-100 group">
                    <img src={url} alt="" className="w-full h-full object-cover" />
                    <button 
                      type="button" 
                      onClick={() => removeImage(index)}
                      className="absolute inset-0 bg-red-500/80 text-white opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center font-bold text-xs"
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>

              <div className="pt-4 border-t border-slate-100">
                <label className="block w-full py-3 px-4 bg-brand-50 text-brand-600 text-center rounded-xl font-bold cursor-pointer hover:bg-brand-100 transition-colors">
                  <input type="file" multiple accept="image/*" className="hidden" onChange={handleFileSelect} />
                  {selectedFiles.length > 0 ? `${selectedFiles.length} Selected` : '+ Add Photos'}
                </label>
                
                {selectedFiles.length > 0 && (
                  <button 
                    type="button" 
                    onClick={handleUploadImages} 
                    disabled={uploading}
                    className="w-full mt-2 text-xs font-bold text-slate-400 hover:text-brand-500 underline uppercase tracking-tighter"
                  >
                    {uploading ? 'Uploading...' : 'Click to confirm upload'}
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditPet;
