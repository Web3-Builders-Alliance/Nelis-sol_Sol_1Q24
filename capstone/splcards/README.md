<h1>SPL Cards & Wallet Policy protocol</h1>
<p>Submission: </p>
<p>Twitter: <a href="https://twitter.com/SPLcards" target="_blank">https://twitter.com/SPLcards</a></p>
<p>Website: <a href="https://www.splcards.com" target="_blank">https://www.splcards.com</a></p>

<br />
<h2>Renaissance hackathon submission</h2>
Program is deployed on devnet:
<code>6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew</code>

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
<p>Code of the Android app in the <a href="">android-app folder</a></p>


