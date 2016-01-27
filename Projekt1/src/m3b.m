ps = [0.25, 0.75];

pr = [0.02, 0.98];

pe(1, 2, 2) = 0.01;
pe(1, 1, 2) = 0.8;
pe(1, 2, 1) = 0.5;
pe(1, 1, 1) = 0.9;
for s = 1:2
	for r = 1:2
		pe(2, s, r) = 1-pe(1, s, r);
	end
end

pg(1, 1) = 0.7;
pg(1, 2) = 0.01;
for r = 1:2
	pg(2, r) = 1-pg(1, r);
end

for s = 1:2
	for r = 1:2
		for e = 1:2
			for g = 1:2
				s
				r
				e
				g
				ps(s)*pr(r)*pe(e, s, r)*pg(g, r)
			end
		end
	end
end