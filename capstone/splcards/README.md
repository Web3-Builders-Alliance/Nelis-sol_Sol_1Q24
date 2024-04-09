<h1>SPL Cards & Wallet Policy protocol</h1>
<p>Submission: </p>
<p>Twitter: <a href="https://twitter.com/SPLcards" target="_blank">https://twitter.com/SPLcards</a></p>
<p>Website: <a href="https://www.splcards.com" target="_blank">https://www.splcards.com</a></p>

<br />
<h2>Renaissance hackathon submission</h2>
Program is deployed on devnet:
<code>6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew</code><br />
<a href="https://explorer.solana.com/address/6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew?cluster=custom&customUrl=https%3A%2F%2Fapi.devnet.solana.com" target="_blank">See Solana Explorer</a>

<hr />

Build project<br />
<code>anchor build</code>

Start local Solana validator<br />
<code>solana-test-validator</code>

Deploy program on local Solana validator<br />
<code>anchor deploy</code>

A succesful deploy leads to the following message:<br />
<code>Program Id: "your-program-id"</code><br />
<code>Deploy success</code>

Update the program id in two files:<br />
<code>lib.rs</code><br />
<code>anchor.toml</code>

Build anchor program with the new program id:<br />
<code>anchor build</code>

Test the program on your local Solana validator:<br />
<code>anchor test --skip-local-validator</code>

<br />
The tests will give you transaction signature to track the transactions on localnet or devnet:
<img width="1000" alt="Screenshot 2024-04-09 at 04 07 59" src="https://github.com/Web3-Builders-Alliance/Nelis-sol_Sol_1Q24/assets/96995954/41ac2ee8-99c2-4074-abc3-a847a3b0a5a1">



<br /><br />
<p><b>Code of the Android app in the <a href="https://github.com/Web3-Builders-Alliance/Nelis-sol_Sol_1Q24/tree/main/capstone/splcards/android-app" target="_blank">android-app folder</a></b></p>
<img width="500" alt="Screenshot 2024-04-09 at 04 07 59" src="https://github.com/Web3-Builders-Alliance/Nelis-sol_Sol_1Q24/assets/96995954/7b331ce4-75b3-4bd3-8ce9-9e28645bfdd4">
<img width="500" alt="Screenshot 2024-04-09 at 04 07 59" src="https://github.com/Web3-Builders-Alliance/Nelis-sol_Sol_1Q24/assets/96995954/c157aea3-08f4-4d97-8281-98eec9f9cf83">

<br /><br />



