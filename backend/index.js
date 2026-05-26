const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(cors());
app.use(express.json());

// =============================================
// PERSISTENCIA EN ARCHIVO data.json
// =============================================

const DATA_FILE = path.join(__dirname, 'data.json');

// Lee los datos desde el archivo
function cargarDatos() {
  const raw = fs.readFileSync(DATA_FILE, 'utf-8');
  return JSON.parse(raw);
}

// Guarda los datos en el archivo
function guardarDatos(datos) {
  fs.writeFileSync(DATA_FILE, JSON.stringify(datos, null, 2), 'utf-8');
}

// =============================================
// ENDPOINTS DE METAS
// =============================================

app.get('/metas', (req, res) => {
  const { metas, pagos } = cargarDatos();
  const metasConResumen = metas.map(meta => {
    const pagosDeMeta = pagos.filter(p => p.metaId === meta.id);
    const totalRecaudado = pagosDeMeta.reduce((sum, p) => sum + p.monto, 0);
    const porcentaje = Math.round((totalRecaudado / meta.valorTotal) * 100);
    return { ...meta, totalRecaudado, porcentajeCumplido: porcentaje, porcentajeFaltante: 100 - porcentaje };
  });
  res.json(metasConResumen);
});

app.get('/metas/:id', (req, res) => {
  const { metas, pagos } = cargarDatos();
  const id = parseInt(req.params.id);
  const meta = metas.find(m => m.id === id);
  if (!meta) return res.status(404).json({ error: 'Meta no encontrada' });

  const pagosDeMeta = pagos.filter(p => p.metaId === id);
  const totalRecaudado = pagosDeMeta.reduce((sum, p) => sum + p.monto, 0);
  const porcentaje = Math.round((totalRecaudado / meta.valorTotal) * 100);

  const aportesPorMiembro = meta.miembros.map(miembro => {
    const pagosMiembro = pagosDeMeta.filter(p => p.miembroId === miembro.id);
    const totalMiembro = pagosMiembro.reduce((sum, p) => sum + p.monto, 0);
    return { ...miembro, totalAportado: totalMiembro, pagos: pagosMiembro };
  });

  res.json({ ...meta, totalRecaudado, porcentajeCumplido: porcentaje, porcentajeFaltante: 100 - porcentaje, aportesPorMiembro });
});

app.post('/metas', (req, res) => {
  const datos = cargarDatos();
  const { nombre, descripcion, valorTotal, foto, miembros } = req.body;
  if (!nombre || !valorTotal) return res.status(400).json({ error: 'nombre y valorTotal son requeridos' });

  const nuevaMeta = {
    id: datos.nextMetaId++,
    nombre,
    descripcion: descripcion || '',
    valorTotal,
    foto: foto || 'https://via.placeholder.com/300x200?text=Meta',
    miembros: (miembros || []).map(m => ({ id: datos.nextMiembroId++, nombre: m.nombre }))
  };

  datos.metas.push(nuevaMeta);
  guardarDatos(datos);
  res.status(201).json(nuevaMeta);
});

app.post('/metas/:id/miembros', (req, res) => {
  const datos = cargarDatos();
  const id = parseInt(req.params.id);
  const meta = datos.metas.find(m => m.id === id);
  if (!meta) return res.status(404).json({ error: 'Meta no encontrada' });

  const { nombre } = req.body;
  if (!nombre) return res.status(400).json({ error: 'nombre es requerido' });

  const nuevoMiembro = { id: datos.nextMiembroId++, nombre };
  meta.miembros.push(nuevoMiembro);
  guardarDatos(datos);
  res.status(201).json(nuevoMiembro);
});

// =============================================
// ENDPOINTS DE PAGOS
// =============================================

app.get('/metas/:id/pagos', (req, res) => {
  const { metas, pagos } = cargarDatos();
  const metaId = parseInt(req.params.id);
  const meta = metas.find(m => m.id === metaId);
  if (!meta) return res.status(404).json({ error: 'Meta no encontrada' });

  const pagosDeMeta = pagos.filter(p => p.metaId === metaId).map(pago => {
    const miembro = meta.miembros.find(m => m.id === pago.miembroId);
    return { ...pago, nombreMiembro: miembro ? miembro.nombre : 'Desconocido' };
  });
  res.json(pagosDeMeta);
});

app.post('/pagos', (req, res) => {
  const datos = cargarDatos();
  const { metaId, miembroId, monto, descripcion } = req.body;
  if (!metaId || !miembroId || !monto) return res.status(400).json({ error: 'metaId, miembroId y monto son requeridos' });

  const meta = datos.metas.find(m => m.id === metaId);
  if (!meta) return res.status(404).json({ error: 'Meta no encontrada' });

  const miembro = meta.miembros.find(m => m.id === miembroId);
  if (!miembro) return res.status(404).json({ error: 'Miembro no encontrado' });

  const nuevoPago = {
    id: datos.nextPagoId++,
    metaId,
    miembroId,
    monto,
    fecha: new Date().toISOString().split('T')[0],
    descripcion: descripcion || 'Sin descripción'
  };

  datos.pagos.push(nuevoPago);
  guardarDatos(datos);
  res.status(201).json({ ...nuevoPago, nombreMiembro: miembro.nombre });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`✅ Servidor corriendo en http://localhost:${PORT}`);
  console.log(`💾 Datos guardados en: ${DATA_FILE}`);
});
