% HMM Forward algoritm
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

% Output
n_states=length(T(1,:));
n_observations=length(o);

% Algorithm
F = zeros(n_observations+1, n_states);
F(1,:) = pi;
for i = 2:n_observations+1
    F(i,:) = (F(i-1,:) * T * diag(E(:, o(i-1))))';
    F(i,:) = F(i,:) / sum(F(i,:));
end

B = zeros(n_observations+1, n_states);
B(end,:) = ones(1, n_states);
B(end,:) = B(end,:) / sum(B(end,:));
for i = n_observations:-1:1
    B(i,:) = T * diag(E(:, o(i))) * B(i+1,:)';
    B(i,:) = B(i,:) / sum(B(i,:));
end

FB = zeros(n_observations+1, n_states);
for i = 1:n_observations+1
    FB(i,:) = F(i,:) .* B(i,:);
    FB(i,:) = FB(i,:) / sum(FB(i,:));
end

F = [(0:n_observations)',F]
B = [(0:n_observations)', B]
FB = [(0:n_observations)',FB]