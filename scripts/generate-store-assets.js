#!/usr/bin/env node
/**
 * Generates store assets for Sunset Cafe:
 *  - play-site/assets/icon-512.svg   → App icon (512×512)
 *  - play-site/assets/feature-1024x500.svg → Feature graphic (1024×500)
 *
 * Run: node scripts/generate-store-assets.js
 * Then open the SVGs in a browser and screenshot, or convert with:
 *   rsvg-convert -w 512 -h 512 play-site/assets/icon-512.svg -o play-site/assets/icon-512.png
 */

const fs = require('fs');
const path = require('path');

const outDir = path.join(__dirname, '..', 'play-site', 'assets');
fs.mkdirSync(outDir, { recursive: true });

// ── App Icon 512×512 ──────────────────────────────────────────────────────────
const icon = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 512 512">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%"   stop-color="#c46b2a"/>
      <stop offset="100%" stop-color="#f59e0b"/>
    </linearGradient>
    <linearGradient id="cup" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%"   stop-color="#fff8f0"/>
      <stop offset="100%" stop-color="#ffe0b2"/>
    </linearGradient>
  </defs>

  <!-- Background circle -->
  <rect width="512" height="512" rx="110" fill="url(#bg)"/>

  <!-- Shadow under cup -->
  <ellipse cx="256" cy="370" rx="95" ry="18" fill="rgba(0,0,0,0.18)"/>

  <!-- Cup body -->
  <path d="M185 220 L195 355 Q196 370 213 372 L299 372 Q316 370 317 355 L327 220 Z"
        fill="url(#cup)" stroke="#d4813a" stroke-width="5"/>

  <!-- Cup rim -->
  <rect x="178" y="208" width="156" height="22" rx="11" fill="#fff" stroke="#d4813a" stroke-width="4"/>

  <!-- Steam lines -->
  <path d="M220 185 Q228 160 220 140" fill="none" stroke="white" stroke-width="7"
        stroke-linecap="round" opacity="0.75"/>
  <path d="M256 178 Q264 150 256 128" fill="none" stroke="white" stroke-width="7"
        stroke-linecap="round" opacity="0.75"/>
  <path d="M292 185 Q300 160 292 140" fill="none" stroke="white" stroke-width="7"
        stroke-linecap="round" opacity="0.75"/>

  <!-- Coffee surface inside cup -->
  <ellipse cx="256" cy="232" rx="60" ry="10" fill="#a0522d" opacity="0.6"/>

  <!-- App name -->
  <text x="256" y="430" font-family="Arial Black, Arial" font-weight="900"
        font-size="52" fill="white" text-anchor="middle"
        filter="drop-shadow(0 2px 4px rgba(0,0,0,0.3))">SUNSET</text>
  <text x="256" y="480" font-family="Arial Black, Arial" font-weight="900"
        font-size="40" fill="rgba(255,255,255,0.88)" text-anchor="middle">CAFE</text>
</svg>`;

// ── Feature Graphic 1024×500 ──────────────────────────────────────────────────
const feature = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="500" viewBox="0 0 1024 500">
  <defs>
    <linearGradient id="bg2" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%"   stop-color="#c46b2a"/>
      <stop offset="60%"  stop-color="#e07b35"/>
      <stop offset="100%" stop-color="#f59e0b"/>
    </linearGradient>
  </defs>

  <!-- Background -->
  <rect width="1024" height="500" fill="url(#bg2)"/>

  <!-- Decorative circles -->
  <circle cx="900" cy="80"  r="180" fill="rgba(255,255,255,0.07)"/>
  <circle cx="950" cy="420" r="130" fill="rgba(255,255,255,0.05)"/>
  <circle cx="60"  cy="400" r="160" fill="rgba(0,0,0,0.06)"/>

  <!-- Left: big cup icon -->
  <g transform="translate(100,80) scale(1.1)">
    <ellipse cx="140" cy="305" rx="80" ry="14" fill="rgba(0,0,0,0.15)"/>
    <path d="M80 170 L90 295 Q91 308 104 309 L176 309 Q189 308 190 295 L200 170 Z"
          fill="rgba(255,255,255,0.92)" stroke="rgba(255,255,255,0.6)" stroke-width="3"/>
    <rect x="74" y="160" width="132" height="18" rx="9"
          fill="white" stroke="rgba(255,255,255,0.5)" stroke-width="2"/>
    <path d="M105 148 Q112 128 105 110" fill="none" stroke="rgba(255,255,255,0.7)"
          stroke-width="6" stroke-linecap="round"/>
    <path d="M140 142 Q147 120 140 100" fill="none" stroke="rgba(255,255,255,0.7)"
          stroke-width="6" stroke-linecap="round"/>
    <path d="M175 148 Q182 128 175 110" fill="none" stroke="rgba(255,255,255,0.7)"
          stroke-width="6" stroke-linecap="round"/>
  </g>

  <!-- Center: App name + tagline -->
  <text x="512" y="190" font-family="Arial Black, Arial" font-weight="900"
        font-size="88" fill="white" text-anchor="middle"
        filter="drop-shadow(0 4px 12px rgba(0,0,0,0.3))">Sunset Cafe</text>
  <text x="512" y="255" font-family="Arial, sans-serif" font-weight="400"
        font-size="32" fill="rgba(255,255,255,0.9)" text-anchor="middle">
    Scan. Order. Enjoy.
  </text>

  <!-- Feature badges -->
  <g>
    <!-- Badge 1 -->
    <rect x="290" y="295" width="200" height="54" rx="27"
          fill="rgba(255,255,255,0.18)" stroke="rgba(255,255,255,0.4)" stroke-width="2"/>
    <text x="390" y="328" font-family="Arial" font-size="20" font-weight="700"
          fill="white" text-anchor="middle">📱 QR Ordering</text>

    <!-- Badge 2 -->
    <rect x="412" y="295" width="200" height="54" rx="27"
          fill="rgba(255,255,255,0.18)" stroke="rgba(255,255,255,0.4)" stroke-width="2"/>
    <text x="512" y="328" font-family="Arial" font-size="20" font-weight="700"
          fill="white" text-anchor="middle">🍛 Live Menu</text>

    <!-- Badge 3 -->
    <rect x="534" y="295" width="200" height="54" rx="27"
          fill="rgba(255,255,255,0.18)" stroke="rgba(255,255,255,0.4)" stroke-width="2"/>
    <text x="634" y="328" font-family="Arial" font-size="20" font-weight="700"
          fill="white" text-anchor="middle">🔔 Fast Service</text>
  </g>

  <!-- Bottom tagline -->
  <text x="512" y="440" font-family="Arial" font-size="22"
        fill="rgba(255,255,255,0.7)" text-anchor="middle">
    The smart way to dine in — for customers, staff, and admins
  </text>
</svg>`;

fs.writeFileSync(path.join(outDir, 'icon-512.svg'), icon, 'utf8');
fs.writeFileSync(path.join(outDir, 'feature-1024x500.svg'), feature, 'utf8');

// ── HTML preview helper ───────────────────────────────────────────────────────
const preview = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <title>Store Asset Preview</title>
  <style>
    body { background:#333; font-family:Arial; color:#fff; padding:40px; }
    h2 { margin:24px 0 12px; color:#f59e0b; }
    .box { display:inline-block; background:#555; padding:16px; border-radius:12px; margin:8px; }
    .round img { border-radius:22%; }
  </style>
</head>
<body>
  <h1>Sunset Cafe — Store Asset Preview</h1>

  <h2>App Icon 512×512</h2>
  <div class="box round">
    <img src="icon-512.svg" width="200" height="200" alt="App icon"/>
  </div>
  <div class="box round">
    <img src="icon-512.svg" width="108" height="108" alt="App icon small"/>
  </div>

  <h2>Feature Graphic 1024×500</h2>
  <div class="box">
    <img src="feature-1024x500.svg" width="512" height="250" alt="Feature graphic"/>
  </div>

  <p style="margin-top:32px; color:#aaa; font-size:14px;">
    To export as PNG for Play Console:<br/>
    <code>rsvg-convert -w 512 -h 512 icon-512.svg -o icon-512.png</code><br/>
    <code>rsvg-convert -w 1024 -h 500 feature-1024x500.svg -o feature-1024x500.png</code><br/>
    Or open each SVG in Chrome → right-click → Save Image As PNG.
  </p>
</body>
</html>`;

fs.writeFileSync(path.join(outDir, 'preview.html'), preview, 'utf8');

console.log('✅ Store assets generated:');
console.log('   play-site/assets/icon-512.svg');
console.log('   play-site/assets/feature-1024x500.svg');
console.log('   play-site/assets/preview.html');
console.log('');
console.log('Open play-site/assets/preview.html in Chrome to review them.');
console.log('Right-click each image → "Save image as" to get PNG files for Play Console.');

