import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { useTenant } from '../context/TenantContext'

const emptyForm = {
  fullName: '',
  email: '',
  password: '',
  role: 'DEVELOPER',
  isActive: true,
}

export default function Users() {
  const { selectedTenant } = useTenant()
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  async function loadUsers() {
    const data = await api('/api/users')
    setUsers(data.filter((user) => user.role !== 'SUPER_ADMIN'))
  }

  useEffect(() => {
    loadUsers().catch((err) => setError(err.message))
  }, [selectedTenant?.id])

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setMessage('')

    try {
      await api('/api/users', {
        method: 'POST',
        body: JSON.stringify(form),
      })
      setMessage('User created')
      setForm(emptyForm)
      await loadUsers()
    } catch (err) {
      setError(err.message)
    }
  }

  async function toggleActive(user) {
    try {
      await api(`/api/users/${user.id}`, {
        method: 'PUT',
        body: JSON.stringify({
          fullName: user.fullName,
          role: user.role,
          isActive: !user.isActive,
        }),
      })
      await loadUsers()
    } catch (err) {
      setError(err.message)
    }
  }

  async function deleteUser(id) {
    if (!window.confirm('Delete this user?')) return

    try {
      await api(`/api/users/${id}`, { method: 'DELETE' })
      setMessage('User deleted')
      await loadUsers()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="page">
      <header className="page-header">
        <h2>Users</h2>
        <p className="muted">
          Manage TENANT_ADMIN and DEVELOPER users for {selectedTenant?.tenantName}
        </p>
      </header>

      {error && <div className="error-box">{error}</div>}
      {message && <div className="success-box">{message}</div>}

      <section className="panel">
        <h3>Add User</h3>
        <form className="form-grid" onSubmit={handleSubmit}>
          <label>
            Full Name
            <input
              value={form.fullName}
              onChange={(e) => setForm({ ...form, fullName: e.target.value })}
              required
            />
          </label>

          <label>
            Email
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              required
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </label>

          <label>
            Role
            <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
              <option value="TENANT_ADMIN">Tenant Admin</option>
              <option value="DEVELOPER">Developer</option>
            </select>
          </label>

          <div className="form-actions">
            <button type="submit" className="btn-primary">Add User</button>
          </div>
        </form>
      </section>

      <section className="panel">
        <h3>Tenant Users</h3>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Last Login</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.fullName}</td>
                  <td>{user.email}</td>
                  <td>{user.role}</td>
                  <td>
                    <span className={`pill ${user.isActive ? 'pill-green' : 'pill-gray'}`}>
                      {user.isActive ? 'Active' : 'Disabled'}
                    </span>
                  </td>
                  <td>
                    {user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : 'Never'}
                  </td>
                  <td className="actions">
                    <button type="button" className="btn-link" onClick={() => toggleActive(user)}>
                      {user.isActive ? 'Disable' : 'Enable'}
                    </button>
                    <button type="button" className="btn-link danger" onClick={() => deleteUser(user.id)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
