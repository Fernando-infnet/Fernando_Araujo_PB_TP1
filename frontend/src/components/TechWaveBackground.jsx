const wavePaths = [
  'M-80 285 C 165 270, 245 88, 520 82 S 860 285, 1120 242 S 1390 112, 1680 220',
  'M-80 296 C 170 280, 260 106, 528 100 S 855 276, 1115 252 S 1400 132, 1680 229',
  'M-80 307 C 176 292, 276 126, 540 120 S 850 266, 1110 261 S 1410 153, 1680 239',
  'M-80 318 C 184 303, 290 146, 552 140 S 846 258, 1105 272 S 1420 174, 1680 250',
  'M-80 330 C 190 315, 307 168, 566 161 S 842 251, 1100 282 S 1430 195, 1680 262',
  'M-80 342 C 198 327, 325 190, 580 184 S 838 245, 1096 293 S 1442 217, 1680 274',
  'M-80 354 C 206 340, 344 214, 595 207 S 834 241, 1092 305 S 1452 239, 1680 287',
  'M-80 366 C 215 352, 365 239, 610 232 S 830 238, 1088 318 S 1462 263, 1680 300',
  'M-80 378 C 224 364, 386 265, 626 258 S 826 237, 1084 331 S 1472 288, 1680 314'
];

function TechWaveBackground() {
  return (
    <div className="tech-wave-background" aria-hidden="true">
      <svg viewBox="0 0 1600 460" preserveAspectRatio="none">
        <defs>
          <linearGradient id="waveStroke" x1="0" y1="0" x2="1" y2="0">
            <stop offset="0" stopColor="#0ac29d" stopOpacity="0" />
            <stop offset=".2" stopColor="#17d3ad" stopOpacity=".38" />
            <stop offset=".52" stopColor="#26e0b9" stopOpacity=".55" />
            <stop offset=".82" stopColor="#12b995" stopOpacity=".24" />
            <stop offset="1" stopColor="#0aa280" stopOpacity="0" />
          </linearGradient>
          <filter id="waveGlow" x="-10%" y="-30%" width="120%" height="160%">
            <feGaussianBlur stdDeviation="4" result="blur" />
            <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
          </filter>
          <pattern id="scanLines" width="20" height="22" patternUnits="userSpaceOnUse">
            <path d="M0 1H20" stroke="#4bdcc1" strokeOpacity=".055" strokeWidth="1" />
          </pattern>
        </defs>
        <rect width="1600" height="460" fill="url(#scanLines)" />
        <g className="wave-strands" fill="none" stroke="url(#waveStroke)" strokeWidth="1.4">
          {wavePaths.map((path, index) => <path d={path} key={index} opacity={1 - index * .075} />)}
        </g>
        <path className="wave-highlight" d={wavePaths[0]} fill="none" stroke="url(#waveStroke)" strokeWidth="2.3" filter="url(#waveGlow)" />
      </svg>
    </div>
  );
}

export default TechWaveBackground;
