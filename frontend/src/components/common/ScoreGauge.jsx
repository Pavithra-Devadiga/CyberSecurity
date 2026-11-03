import React from 'react';

export default function ScoreGauge({ score, grade, size = 'md' }) {
  const numericScore = typeof score === 'number' ? score : 0;

  // Grade color
  const getGradeColor = (g) => {
    switch (g) {
      case 'A+':
      case 'A':
        return 'text-emerald-400 border-emerald-500/30 bg-emerald-500/10 stroke-emerald-500';
      case 'B':
        return 'text-cyan-400 border-cyan-500/30 bg-cyan-500/10 stroke-cyan-500';
      case 'C':
        return 'text-yellow-400 border-yellow-500/30 bg-yellow-500/10 stroke-yellow-500';
      case 'D':
        return 'text-orange-400 border-orange-500/30 bg-orange-500/10 stroke-orange-500';
      default:
        return 'text-rose-400 border-rose-500/30 bg-rose-500/10 stroke-rose-500';
    }
  };

  const radius = 38;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (numericScore / 100) * circumference;

  const isSmall = size === 'sm';

  return (
    <div className="flex items-center gap-3">
      <div className={`relative flex items-center justify-center ${isSmall ? 'w-14 h-14' : 'w-24 h-24'}`}>
        <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
          {/* Background circle */}
          <circle
            cx="50"
            cy="50"
            r={radius}
            className="stroke-slate-800"
            strokeWidth={isSmall ? '8' : '7'}
            fill="transparent"
          />
          {/* Progress circle */}
          <circle
            cx="50"
            cy="50"
            r={radius}
            className={`transition-all duration-1000 ease-out ${getGradeColor(grade).split(' ')[3]}`}
            strokeWidth={isSmall ? '8' : '7'}
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            fill="transparent"
          />
        </svg>

        <div className="absolute flex flex-col items-center justify-center">
          <span className={`font-bold tracking-tight text-white ${isSmall ? 'text-sm' : 'text-xl'}`}>
            {score !== null && score !== undefined ? score : '--'}
          </span>
          {!isSmall && <span className="text-[10px] text-slate-400 uppercase font-medium">/ 100</span>}
        </div>
      </div>

      {grade && (
        <div className="flex flex-col">
          <span className="text-xs text-slate-400 uppercase tracking-wider font-semibold">Security Grade</span>
          <div className="flex items-baseline gap-1.5 mt-0.5">
            <span className={`text-2xl font-extrabold px-2.5 py-0.5 rounded-md border ${getGradeColor(grade)}`}>
              {grade}
            </span>
            <span className="text-xs text-slate-400 font-medium">
              {numericScore >= 85 ? 'Strong Posture' : numericScore >= 60 ? 'Needs Attention' : 'High Risk'}
            </span>
          </div>
        </div>
      )}
    </div>
  );
}
