export interface SubmissionComparison {
  similarity: number;
}

export interface SubmissionComparisonDetail extends SubmissionComparison {
  matches: Match[];
}

export interface Match {
  firstFile: string;
  secondFile: string;
  firstStart: number;
  firstEnd: number;
  secondStart: number;
  secondEnd: number;
  tokens: number;
}

export interface Cluster {
  averageSimilarity: number;
  strength: number;
  members: Set<string>;
}

export interface Metric {
  distribution: Distribution;
  topComparison: SubmissionComparison[];
}

export type Distribution = [
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
  number,
];

export type Comparisons = Record<
  string,
  Record<string, SubmissionComparisonDetail>
>;

export type Submissions = Record<
  string,
  {
    id: string;
    email: string;
    givenName: string;
    familyName: string;
  }
>;

export interface PlagiarismReport {
  exerciseId: string;
  executionDate: Date;
  submissions: Submissions;
  comparisons: Comparisons;
  clusters: Cluster[];
  averageMetrics: Metric;
  maxMetrics: Metric;
}
