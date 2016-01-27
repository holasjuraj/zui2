% Viterbi algorithm
clear all;

% T(N,N)=transition probability matrix
T = [[0.7, 0.3]; ... % if fair coin, there's 70% chance of fair coin in next round
    [0.2, 0.8]];     % biased coin

% E(N,M)=Emission matrix
E = [[0.5, 0.5]; ... % fair coin
    [0.2, 0.8]];     % biased coin

% pi=initial probability matrix
pi = [0.6, 0.4];

% O=Given observation sequence labelled in numerics
% 1=hlava, 2=znak
o = [1,2,1,1,2,1,2,2,2];
% o = [1,1,1,2,2,2,1,2,2,2,1,1,1,2,2,2,2,2];

% Output
n_states=length(T(1,:));
n_observations=length(o);

% Algorithm
A = zeros(n_states, n_observations); % probability of seq. ending in particular state
from = zeros(n_states, n_observations); % z ktoreho stavu som dosiel do j v case t
mx = zeros(1, n_observations); % list of indexes of states with max. prob.

A(:, 1) = pi' .* E(:,o(1));
% A(:, 1) = A(:, 1) / sum(A(:, 1));
for t=2:n_observations
    for j=1:n_states
        a = A(:,t-1) .* T(:,j) .* E(j, o(t));
        A(j,t) = max(a);
        from(j,t) = (1:n_states) * (a == max(a));
    end
    % A(:, t) = A(:, t) / sum(A(:, t));
end

mx(n_observations) = (1:n_states) * (A(:,n_observations) == max(A(:,n_observations)));
for t = n_observations-1:-1:1
    mx(t) = from(mx(t+1),t+1);
end

A = A'
from = from'
mx
A(n_observations,mx(n_observations))